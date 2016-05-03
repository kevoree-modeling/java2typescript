
package org.kevoree.modeling.java2typescript.translators.expression;

import com.intellij.psi.*;
import org.kevoree.modeling.java2typescript.context.TranslationContext;
import org.kevoree.modeling.java2typescript.helper.TypeHelper;

public class MethodCallExpressionTranslator {

    public static void translate(PsiMethodCallExpression element, TranslationContext ctx) {
        PsiReferenceExpression methodExpr = element.getMethodExpression();
        boolean hasBeenTransformed = tryNativeTransform(element, ctx);
        if (!hasBeenTransformed) {
            ReferenceExpressionTranslator.translate(methodExpr, ctx);
            ctx.append('(');
            printParameters(element.getArgumentList().getExpressions(), ctx);
            ctx.append(")");
        }
    }

    private static void printParameters(PsiExpression[] arguments, TranslationContext ctx) {
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof PsiReferenceExpression) {
                ReferenceExpressionTranslator.translate((PsiReferenceExpression) arguments[i], ctx);
            } else {
                ExpressionTranslator.translate(arguments[i], ctx);
            }
            if (i != arguments.length - 1) {
                ctx.append(", ");
            }
        }
    }

    private static boolean tryNativeTransform(PsiMethodCallExpression element, TranslationContext ctx) {

        // a.b.c.method()
        PsiReferenceExpression methodExpression = element.getMethodExpression();
        // a.b.c
        PsiExpression methodQualifierExpression = methodExpression.getQualifierExpression();

        if (methodQualifierExpression != null) {
            if (methodQualifierExpression.getType() != null) {
                if (methodQualifierExpression.getType().getCanonicalText().equals("String")) {
                    if (methodExpression.getReferenceName() != null) {


                        if (methodExpression.getReferenceName().equals("length")) {
                            ExpressionTranslator.translate(methodQualifierExpression, ctx);
                            if (element.getArgumentList().getExpressions().length == 0) {
                                ctx.append(".length");
                                return true;
                            }


                        } else if (methodExpression.getReferenceName().equals("codePointAt")) {
                            ExpressionTranslator.translate(methodQualifierExpression, ctx);
                            ctx.append(".charCodeAt(");
                            PsiExpression[] arguments = element.getArgumentList().getExpressions();
                            for (int i = 0; i < arguments.length; i++) {
                                ExpressionTranslator.translate(arguments[i], ctx);
                                if (i != arguments.length - 1) {
                                    ctx.append(", ");
                                }
                            }
                            ctx.append(")");
                            ctx.needsJava(TypeHelper.javaTypes.get("String"));
                            return true;


                        } else if (methodExpression.getReferenceName().equals("concat")) {

                            ExpressionTranslator.translate(methodQualifierExpression, ctx);
                            ctx.append(" + ");
                            ExpressionTranslator.translate(element.getArgumentList().getExpressions()[0], ctx);
                            return true;
                        }
                    }


                } else if (methodQualifierExpression.getType().getCanonicalText().equals("Exception")
                        || methodQualifierExpression.getType().getCanonicalText().equals("Error")
                        || methodQualifierExpression.getType().getCanonicalText().equals("Throwable")) {
                    //error .printStackTrace
                    if (methodExpression.getReferenceName() != null &&
                            methodExpression.getReferenceName().equals("printStackTrace")) {
                        ctx.append("console.error(");
                        ExpressionTranslator.translate(methodQualifierExpression, ctx);
                        ctx.append(")");
                        return true;
                    }
                } else if (methodQualifierExpression.getType().getCanonicalText().equals("PrintStream")) {
                    //error .printStackTrace
                    if (methodExpression.getReferenceName() != null &&
                            methodExpression.getReferenceName().equals("println")) {
                        ctx.append("console.log(");
                        ExpressionTranslator.translate(methodQualifierExpression, ctx);
                        ctx.append(")");
                        return true;
                    }
                }
            } else if (methodQualifierExpression instanceof PsiReferenceExpression) {
                PsiReferenceExpression objectRef = (PsiReferenceExpression) methodQualifierExpression;
                if (objectRef.getQualifier() != null) {
                    if (objectRef.getQualifier().getText().equals("System")) {
                        if (objectRef.getReferenceName().equals("out")) {
                            ctx.append("console.log(");
                            ExpressionTranslator.translate(element.getArgumentList().getExpressions()[0], ctx);
                            ctx.append(")");
                            return true;
                        } else if (objectRef.getReferenceName().equals("err")) {
                            ctx.append("console.error(");
                            ExpressionTranslator.translate(element.getArgumentList().getExpressions()[0], ctx);
                            ctx.append(")");
                            return true;
                        }
                    }
                }

                /*
                if(methodQualifierExpression.getReferenceName().)
                if(methodQualifierExpression.getReference().getCanonicalText().equals("System.out")) {

                }
                */
            }
        }

        /*
        if (element.getText().matches("^(java\\.lang\\.)?System\\.out.*$")) {
            ctx.append("console.log(");
            PsiExpression[] arguments = element.getArgumentList().getExpressions();
            for (int i = 0; i < arguments.length; i++) {
                ExpressionTranslator.translate(arguments[i], ctx);
                if (i != arguments.length - 1) {
                    ctx.append(", ");
                }
            }
            ctx.append(")");
            return true;

        } else if (element.getText().matches("^(java\\.lang\\.)?System\\.err.*$")) {
            ctx.append("console.error(");
            PsiExpression[] arguments = element.getArgumentList().getExpressions();
            for (int i = 0; i < arguments.length; i++) {
                ExpressionTranslator.translate(arguments[i], ctx);
                if (i != arguments.length - 1) {
                    ctx.append(", ");
                }
            }
            ctx.append(")");
            return true;

        } else if (element.getText().matches("^(java\\.lang\\.)?String\\.join\\(.*$")) {
            ctx.append(TypeHelper.javaTypes.get("String"));
            ctx.append(".join(");
            PsiExpression[] arguments = element.getArgumentList().getExpressions();
            for (int i = 0; i < arguments.length; i++) {
                PsiType type = arguments[i].getType();
                if (type != null && type.getPresentableText().endsWith("[]")) {
                    ctx.append("...");
                }
                if (arguments[i] instanceof PsiReferenceExpression) {
                    ReferenceExpressionTranslator.translate((PsiReferenceExpression) arguments[i], ctx);
                } else {
                    ExpressionTranslator.translate(arguments[i], ctx);
                }
                if (i != arguments.length - 1) {
                    ctx.append(", ");
                }
            }
            ctx.append(")");
            ctx.needsJava(TypeHelper.javaTypes.get("String"));
            return true;
        } else {
            PsiReferenceExpression expr = methodExpression;
            if (expr.getQualifierExpression() instanceof PsiReferenceExpression) {
                PsiElement resolvedRootElem = ((PsiReferenceExpression) expr.getQualifierExpression()).resolve();
                if (resolvedRootElem != null) {
                    if (resolvedRootElem instanceof PsiVariable) {
                        return false;//processRootElem(element, ((PsiVariable) resolvedRootElem).getType(), ctx);

                    } else if (resolvedRootElem instanceof PsiMethodCallExpression) {
                        MethodCallExpressionTranslator.translate((PsiMethodCallExpression) resolvedRootElem, ctx);
                        return true;

                    } else if (resolvedRootElem.getParent() instanceof PsiCatchSection) {
                        ctx.append("console.error(");
                        ExpressionTranslator.translate(methodQualifierExpression, ctx);
                        ctx.append("['stack'])");
                        return true;
                    }
                }
            } else if (expr.getQualifierExpression() != null && expr.getQualifierExpression().getType() != null) {
                if (expr.getQualifierExpression() instanceof PsiMethodCallExpression) {
                    String[] parts = methodExpression.getText().split("\\.");
                    String methodName = parts[parts.length - 1];
                    ctx.append(TypeHelper.javaTypes.get("String"));
                    ctx.append(".");
                    ctx.append(methodName);
                    ctx.append("(");
                    TranslationContext innerCtx = new TranslationContext();
                    MethodCallExpressionTranslator.translate((PsiMethodCallExpression) expr.getQualifierExpression(), innerCtx);
                    ctx.append(innerCtx.getContent());
                    if (element.getArgumentList().getExpressions().length > 0) {
                        ctx.append(", ");
                    }
                    printParameters(element.getArgumentList().getExpressions(), ctx);
                    ctx.append(")");
                    ctx.needsJava(TypeHelper.javaTypes.get("String"));

                    return true;
                }
            }
        }
        */

        return false;
    }

    /*
    private static boolean processRootElem(PsiMethodCallExpression methodCall, PsiType rootType, TranslationContext ctx) {
        if (rootType.getPresentableText().equals("String")) {
            String methodPath = methodCall.getText().split("\\(", 2)[0];
            PsiElement methodNameElement = methodCall.findElementAt(methodPath.lastIndexOf(".") + 1);
            if (methodNameElement != null) {
                String methodName = methodNameElement.getText();
                String rootRef = ReferenceExpressionTranslator.translate(methodCall.getMethodExpression(), ctx, false);
                rootRef = rootRef.substring(0, rootRef.length() - methodName.length());
                if (rootRef.endsWith(".")) {
                    rootRef = rootRef.substring(0, rootRef.length() - 1);
                }

                ctx.append(TypeHelper.javaTypes.get("String"));
                ctx.append(".");
                ctx.append(methodName);
                ctx.append("(");
                ctx.append(rootRef);
                if (methodCall.getArgumentList().getExpressions().length > 0) {
                    ctx.append(", ");
                }
                printParameters(methodCall.getArgumentList().getExpressions(), ctx);
                ctx.append(")");
                ctx.needsJava(TypeHelper.javaTypes.get("String"));

                return true;
            }
        }
        return false;
    }*/
}