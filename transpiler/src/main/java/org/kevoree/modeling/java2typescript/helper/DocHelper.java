package org.kevoree.modeling.java2typescript.helper;

import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import org.kevoree.modeling.java2typescript.metas.DocMeta;
import org.kevoree.modeling.java2typescript.translators.DocTagTranslator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leiko on 23/11/15.
 */
public class DocHelper {

    public static DocMeta process(PsiDocComment comment) {
        DocMeta metas = new DocMeta();
        if (comment != null) {


            ArrayList<PsiDocTag> tags = new ArrayList<>();

            //Collects usual tags
            PsiDocTag[] usualTags = comment.getTags();
            for(int i = 0; i < usualTags.length; i++) {
                tags.add(usualTags[i]);
            }

            //Collects inline tags
            for (PsiElement child : comment.getChildren()) {
                if (child instanceof PsiInlineDocTag) {
                    tags.add((PsiInlineDocTag) child);
                }
            }

            for (PsiDocTag tag : tags) {
                if (tag.getName().equals(DocTagTranslator.NATIVE) && tag.getValueElement() != null && tag.getValueElement().getText().equals(DocTagTranslator.TS)) {
                    metas.nativeActivated = true;

                    String value = tag.getText();
                    String[] lines = value.split("\n");
                    int curlyBraceCounter = 0;
                    for (String line : lines) {
                        String trimmedLine = line.trim();
                        if (trimmedLine.length() > 0 && !trimmedLine.contains("@" + DocTagTranslator.NATIVE) && !trimmedLine.contains("@" + DocTagTranslator.IGNORE)) {
                            if (trimmedLine.charAt(0) == '*') {
                                trimmedLine = trimmedLine.substring(1).trim();
                            }
                            int idx = 0;
                            int nextIdx;
                            while(( nextIdx = trimmedLine.indexOf('{', idx)) != -1) {
                                curlyBraceCounter++;
                                idx = nextIdx+1;
                            }
                            idx = 0;
                            while(( nextIdx = trimmedLine.indexOf('}', idx)) != -1) {
                                curlyBraceCounter--;
                                idx = nextIdx+1;
                            }
                            if(trimmedLine.trim().equals("}") && curlyBraceCounter == -1) {
                                trimmedLine = "";
                            }
                            if (!trimmedLine.isEmpty()) {
                                if(metas.nativeBodyLines == null) {
                                    metas.nativeBodyLines = new ArrayList<>();
                                }
                                metas.nativeBodyLines.add(trimmedLine + "\n");
                            }
                        }
                    }


                }
                if (tag.getName().equals(DocTagTranslator.IGNORE) && tag.getValueElement() != null && tag.getValueElement().getText().equals(DocTagTranslator.TS)) {
                    metas.ignored = true;
                }
                if (tag.getName().equals(DocTagTranslator.OPTIONAL)) {
                    for (PsiElement elem : tag.getDataElements()) {
                        if (!elem.getText().contains(" ")) {
                            metas.optional.add(elem.getText());
                        }
                    }
                }
                if (tag.getName().equals(DocTagTranslator.TS_CALLBACK)) {
                    metas.functionType = true;
                }
            }
        }
        return metas;
    }
}
