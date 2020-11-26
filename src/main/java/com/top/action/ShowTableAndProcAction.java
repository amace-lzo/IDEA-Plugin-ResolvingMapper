package com.top.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.top.domain.MapperInfo;
import com.top.utils.MapperUtil;
import org.junit.Assert;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShowTableAndProcAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] fileList = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        List<PsiElement> xmlList = Stream.of(fileList).filter(obj -> obj instanceof XmlFileImpl).collect(toList());
        if (xmlList.size() == 0) {
            Messages.showErrorDialog("please select the correct file with the 'xml' extension", "FileTypeError");
            return;
        }
        List<MapperInfo> mapperInfos = MapperUtil.getTableAndProcOfMapper(xmlList);
        JFrame jf = new JFrame("Resolving Result");
        JBPanel panel = new JBPanel(new BorderLayout());
        Object[] columnNames = {"MapperName","Procedure", "Table"};
        int tableLength = 0;
        int currentPointer = 0;
        for (MapperInfo mapperInfo : mapperInfos) {
            tableLength += mapperInfo.getMaxLength();
        }
        Object[][] rowData = new Object[tableLength][3];

        for (MapperInfo mapperInfo : mapperInfos) {
            HashSet<String> tableSet = mapperInfo.getTableSet();
            HashSet<String> procSet = mapperInfo.getProcSet();
            String[] tableArr = new String[tableSet.size()];
            String[] procArr = new String[procSet.size()];
            tableSet.toArray(tableArr);
            procSet.toArray(procArr);

            Arrays.sort(tableArr);
            Arrays.sort(procArr);

            int maxRow = mapperInfo.getMaxLength();
            for (int i = currentPointer; i < currentPointer + maxRow; i++) {
                rowData[i][0] = i==currentPointer?mapperInfo.getMapperName():"";
                rowData[i][1] = i < (currentPointer + procArr.length)?procArr[i - currentPointer]:"";
                rowData[i][2] = i < (currentPointer + tableArr.length)?tableArr[i - currentPointer]:"";
            }
            currentPointer += maxRow;
        }

        // create a table,specify all data and header
        JTable table = new JTable(rowData, columnNames);
        // set table show size
        int height = Math.min(rowData.length * 16, 500);
        table.setPreferredScrollableViewportSize(new Dimension(720, height));
        // set table header style
        table.getTableHeader().setBackground(JBColor.darkGray);
        table.getTableHeader().setForeground(JBColor.LIGHT_GRAY);
        // set table content style
        table.setBackground(JBColor.darkGray);
        table.setForeground(JBColor.LIGHT_GRAY);
        // set table column width
        table.getColumnModel().getColumn(0).setPreferredWidth(240);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(240);

        // add header on top of container
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        // add table content on middle of container
        JBScrollPane scrollPane = new JBScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        jf.setContentPane(panel);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}
