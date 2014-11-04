
package com.siliconmint.ts.translator;

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralExpression;

public class LiteralTranslator extends Translator<PsiLiteralExpression> {

    @Override
    public void translate(PsiElementVisitor visitor, PsiLiteralExpression element, TranslationContext ctx) {
        String trimmed = element.getText().trim();
        if( !trimmed.toLowerCase().equals("null") && (trimmed.toLowerCase().endsWith("l") || trimmed.toLowerCase().endsWith("d"))) {
            ctx.append(trimmed.substring(0, trimmed.length()-1));
        } else {
            ctx.append(trimmed);
        }
    }

}
