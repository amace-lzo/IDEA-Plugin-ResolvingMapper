package com.top.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.top.domain.MapperInfo;
import com.top.utils.ExcelUtil;
import com.top.utils.MapperUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ExportTableAndProcAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] fileList = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        List<PsiElement> xmlList = Stream.of(fileList).filter(obj -> obj instanceof XmlFileImpl).collect(toList());
        if (xmlList.size() == 0) {
            Messages.showErrorDialog("please select the correct file with the 'xml' extension", "FileTypeError");
            return;
        }
        List<MapperInfo> mapperInfos = MapperUtil.getTableAndProcOfMapper(xmlList);
        // choose file export path
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(
                false,true,false,false,false,false
        );
        VirtualFile[] virtualFiles = FileChooser.chooseFiles(fileChooserDescriptor, null, null);
        if (virtualFiles.length >0 ) {
            Date currentTime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String dateNowStr = sdf.format(currentTime);
            String path = virtualFiles[0].toString().split("//")[1] + "MapperInfo_" + dateNowStr + ".xlsx";
            try {
                // export excel file to the path
                ExcelUtil.exportExcel(mapperInfos, path);
                Messages.showInfoMessage("Export succeeded", "ExportSucceeded");
            } catch (Exception ex) {
                Messages.showErrorDialog("An error occurred while Exporting,please contact us", "ExportError");
                ex.printStackTrace();
            }
        }
    }

}
