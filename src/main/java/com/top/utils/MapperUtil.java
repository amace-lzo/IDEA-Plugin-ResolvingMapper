package com.top.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.top.domain.MapperInfo;

import java.util.*;

public class MapperUtil {

    /**
     * find table and proc
     * batch processing
     * @param xmlList
     * @return
     */
    public static List<MapperInfo> getTableAndProcOfMapper(List<PsiElement> xmlList) {
        List<MapperInfo> mapperInfos = new ArrayList<>();
        for (PsiElement xmlElement : xmlList) {
            PsiFile psiFile = xmlElement.getContainingFile();
            XmlFile xmlFile = (XmlFile) psiFile;
            if (xmlFile == null) {
                return null;
            }
            HashSet<String> tableSet = new HashSet<>();
            HashSet<String> procSet = new HashSet<>();
            final XmlDocument document = xmlFile.getDocument();
            if (document != null) {
                final XmlTag rootTag = document.getRootTag();
                if (rootTag != null) {
                    // find select
                    final XmlTag[] selects = rootTag.findSubTags("select");
                    for (XmlTag xmlTag : selects) {
                        String text = xmlTag.getValue().getTrimmedText();
                        findTableAndProc(tableSet, procSet, "SELECT", text);
                        // find "if" in text
                        final XmlTag[] ifItems = xmlTag.findSubTags("if");
                        for (XmlTag ifItem : ifItems) {
                            String text1 = ifItem.getValue().getTrimmedText();
                            findTableAndProc(tableSet, procSet, "IF", text1);
                        }
                    }
                    // find update
                    final XmlTag[] updates = rootTag.findSubTags("update");
                    for (XmlTag xmlTag : updates) {
                        String text = xmlTag.getValue().getTrimmedText();
                        findTableAndProc(tableSet, procSet, "UPDATE", text);
                        // find "if" in text
                        final XmlTag[] ifItems = xmlTag.findSubTags("if");
                        for (XmlTag ifItem : ifItems) {
                            String text1 = ifItem.getValue().getTrimmedText();
                            findTableAndProc(tableSet, procSet, "IF", text1);
                        }
                    }
                    // find delete
                    final XmlTag[] deletes = rootTag.findSubTags("delete");
                    for (XmlTag xmlTag : deletes) {
                        String text = xmlTag.getValue().getTrimmedText();
                        findTableAndProc(tableSet, procSet, "DELETE", text);
                        // find "if" in text
                        final XmlTag[] ifItems = xmlTag.findSubTags("if");
                        for (XmlTag ifItem : ifItems) {
                            String text1 = ifItem.getValue().getTrimmedText();
                            findTableAndProc(tableSet, procSet, "IF", text1);
                        }
                    }
                    // find insert
                    final XmlTag[] inserts = rootTag.findSubTags("insert");
                    for (XmlTag xmlTag : inserts) {
                        String text = xmlTag.getValue().getTrimmedText();
                        findTableAndProc(tableSet, procSet, "INSERT", text);
                        // find "if" in text
                        final XmlTag[] ifItems = xmlTag.findSubTags("if");
                        for (XmlTag ifItem : ifItems) {
                            String text1 = ifItem.getValue().getTrimmedText();
                            findTableAndProc(tableSet, procSet, "IF", text1);
                        }
                    }

                    // find sql
                    final XmlTag[] sqls = rootTag.findSubTags("sql");
                    for (XmlTag xmlTag : sqls) {
                        String text = xmlTag.getValue().getTrimmedText();
                        findTableAndProc(tableSet, procSet, "SQL", text);
                        // find "if" in text
                        final XmlTag[] ifItems = xmlTag.findSubTags("if");
                        for (XmlTag ifItem : ifItems) {
                            String text1 = ifItem.getValue().getTrimmedText();
                            findTableAndProc(tableSet, procSet, "IF", text1);
                        }
                    }
                }
            }
            MapperInfo mapperInfo = new MapperInfo();
            mapperInfo.setTableSet(tableSet);
            mapperInfo.setProcSet(procSet);
            mapperInfo.setMapperName(xmlFile.getName());
            mapperInfos.add(mapperInfo);
        }

        return mapperInfos;
    }
    /**
     * find all of table and proc in text
     */
    private static void findTableAndProc(HashSet<String> tableSet, HashSet<String> procSet, String tag, String text) {

        int textLength = text.length();

        if (textLength > 1) {
            // find all of proc in text
            text = text.replaceAll("[{}]","");
            String[] textArr2 = text.split("[()]|\\s+");
            if (textArr2.length > 1) {
                // if the first element is call ,the second element will be proc name
                if("CALL".equals(textArr2[0].toUpperCase())){
                    // exclude proc name of injection
                    if (textArr2[1].charAt(0)!='$') {
                        procSet.add(textArr2[1].toUpperCase());
                    }
                    // there are nothing in text when proc appear
                    return;
                }
            }

            // find all of table name in text
            String[] textArr = text.split("[()]");
            // judge sql is merge
            tag = text.length()>=5?("MERGE".equals(text.substring(0,5).toUpperCase())?"MERGE":tag):tag;
            for (String s : textArr) {
                String[] single = s.split("\\s+");
                for (int j = 0, len2 = single.length; j < len2; j++) {
                    if (isKeyword(tag,single[j])) {
                        if (j < len2 - 1) {
                            // exclude table name of injection
                            // The purpose of judging equal sign is excluding condition in merge
                            if (single[j + 1].charAt(0)!='$'  && !single[j + 1].contains("=")) {
                                tableSet.add(single[j + 1].toUpperCase());
                            }
                            j++;
                            // judge the situation of join using comma
                            while(j < len2 - 1 && single[j + 1].charAt(single[j + 1].length() - 1) == ','){
                                j++;
                                if (j < len2 - 1 && single[j + 1].charAt(0)!='$' && !single[j + 1].contains("=")) {
                                    tableSet.add(single[j + 1].toUpperCase());
                                }
                                j++;
                            }
                        }
                    }
                }
            }
            // remove keyword from tableSet
            tableSet.remove("SET");
            tableSet.remove("DUAL");
        }

    }

    /**
     * judge the word is the keyword of the tag
     * @param tag current tag name,such as "select","update","delete","insert"
     * @param word the word will be judged
     * @return
     */
    private static boolean isKeyword(String tag ,String word){
        boolean isKeyword = false;
        switch (tag.toUpperCase()){
            case "DELETE":
            case "IF":
            case "SELECT":
            case "SQL":
                isKeyword = "FROM".equals(word.toUpperCase()) || "JOIN".equals(word.toUpperCase());
                break;
            case "INSERT":
                isKeyword = "INTO".equals(word.toUpperCase()) ||
                        "FROM".equals(word.toUpperCase()) ||
                        "JOIN".equals(word.toUpperCase());
                break;
            case "UPDATE":
                isKeyword = "INTO".equals(word.toUpperCase()) ||
                        "UPDATE".equals(word.toUpperCase()) ||
                        "FROM".equals(word.toUpperCase()) ||
                        "JOIN".equals(word.toUpperCase());
                break;
            case "MERGE":
                isKeyword = "INTO".equals(word.toUpperCase()) ||
                        "USING".equals(word.toUpperCase()) ||
                        "FROM".equals(word.toUpperCase()) ||
                        "JOIN".equals(word.toUpperCase());
            default:

        }

        return isKeyword;
    }
}
