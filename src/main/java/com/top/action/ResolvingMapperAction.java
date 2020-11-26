package com.top.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.top.utils.MapperUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ResolvingMapperAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            Messages.showErrorDialog("please select the correct file with the 'xml' extension", "FileTypeError");
            return;
        }
        String[] fileNameArr = psiFile.toString().split("\\.");
        String extension = fileNameArr[fileNameArr.length - 1];
        if ("xml".equals(extension)) {
            XmlFile xmlFile = (XmlFile) psiFile;
            Map<String, HashSet<String>> map = getTableAndProcOfMapper(xmlFile);

            JFrame jf = new JFrame("Resolving Result");
            // create JPanel with border layout
            JBPanel panel = new JBPanel(new BorderLayout());
            // table header
            Object[] columnNames = {"Procedures", "Table"};
            // table data
            HashSet<String> tableSet =  map.get("tableSet");
            HashSet<String> procSet =  map.get("procSet");
            String[] tableArr = new String[tableSet.size()];
            String[] procArr = new String[procSet.size()];
            tableSet.toArray(tableArr);
            procSet.toArray(procArr);

            Arrays.sort(tableArr);
            Arrays.sort(procArr);

            int maxRow = Math.max(tableArr.length, procArr.length);

            Object[][] rowData = new Object[maxRow][2];
            for (int i = 0; i < maxRow; i++) {
                if (i < procArr.length) {
                    rowData[i][0] = procArr[i];
                }
                if (i < tableArr.length) {
                    rowData[i][1] = tableArr[i];
                }
            }

            // create a table,specify all data and header
            JTable table = new JTable(rowData, columnNames);
            // set table header style
            table.getTableHeader().setBackground(JBColor.darkGray);
            table.getTableHeader().setForeground(JBColor.LIGHT_GRAY);
            // set table content style
            table.setBackground(JBColor.darkGray);
            table.setForeground(JBColor.LIGHT_GRAY);
            // set table column width
            table.getColumnModel().getColumn(0).setPreferredWidth(240);
            table.getColumnModel().getColumn(1).setPreferredWidth(240);

            // add header on top of container
            panel.add(table.getTableHeader(), BorderLayout.NORTH);
            // add table content on middle of container
            panel.add(table, BorderLayout.CENTER);

            jf.setContentPane(panel);
            jf.pack();
            jf.setLocationRelativeTo(null);
            jf.setVisible(true);
        }else{
            Messages.showErrorDialog("please select the correct file with the 'xml' extension", "FileTypeError");
        }


    }

    private Map<String, HashSet<String>> getTableAndProcOfMapper(XmlFile xmlFile){
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
                    MapperUtil.findTableAndProc(tableSet,procSet,"SELECT", text);
                    // find "if" in text
                    final XmlTag[] ifItems = xmlTag.findSubTags("if");
                    for (XmlTag ifItem : ifItems) {
                        String text1 = ifItem.getValue().getTrimmedText();
                        MapperUtil.findTableAndProc(tableSet,procSet,"IF", text1);
                    }
                }
                // find update
                final XmlTag[] updates = rootTag.findSubTags("update");
                for (XmlTag xmlTag : updates) {
                    String text = xmlTag.getValue().getTrimmedText();
                    MapperUtil.findTableAndProc(tableSet,procSet,"UPDATE", text);
                    // find "if" in text
                    final XmlTag[] ifItems = xmlTag.findSubTags("if");
                    for (XmlTag ifItem : ifItems) {
                        String text1 = ifItem.getValue().getTrimmedText();
                        MapperUtil.findTableAndProc(tableSet,procSet,"IF", text1);
                    }
                }
                // find delete
                final XmlTag[] deletes = rootTag.findSubTags("delete");
                for (XmlTag xmlTag : deletes) {
                    String text = xmlTag.getValue().getTrimmedText();
                    MapperUtil.findTableAndProc(tableSet,procSet,"DELETE", text);
                    // find "if" in text
                    final XmlTag[] ifItems = xmlTag.findSubTags("if");
                    for (XmlTag ifItem : ifItems) {
                        String text1 = ifItem.getValue().getTrimmedText();
                        MapperUtil.findTableAndProc(tableSet,procSet,"IF", text1);
                    }
                }
                // find insert
                final XmlTag[] inserts = rootTag.findSubTags("insert");
                for (XmlTag xmlTag : inserts) {
                    String text = xmlTag.getValue().getTrimmedText();
                    MapperUtil.findTableAndProc(tableSet,procSet,"INSERT", text);
                    // find "if" in text
                    final XmlTag[] ifItems = xmlTag.findSubTags("if");
                    for (XmlTag ifItem : ifItems) {
                        String text1 = ifItem.getValue().getTrimmedText();
                        MapperUtil.findTableAndProc(tableSet,procSet,"IF", text1);
                    }
                }

                // find sql
                final XmlTag[] sqls = rootTag.findSubTags("sql");
                for (XmlTag xmlTag : sqls) {
                    String text = xmlTag.getValue().getTrimmedText();
                    MapperUtil.findTableAndProc(tableSet,procSet,"SQL", text);
                    // find "if" in text
                    final XmlTag[] ifItems = xmlTag.findSubTags("if");
                    for (XmlTag ifItem : ifItems) {
                        String text1 = ifItem.getValue().getTrimmedText();
                        MapperUtil.findTableAndProc(tableSet,procSet,"IF", text1);
                    }
                }
            }
        }
        Map<String, HashSet<String>> result = new HashMap<>();
        result.put("tableSet", tableSet);
        result.put("procSet", procSet);

        return result;
    }
}
