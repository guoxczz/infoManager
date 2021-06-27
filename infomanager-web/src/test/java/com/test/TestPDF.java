package com.test;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
import org.docx4j.Docx4J;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;

public class TestPDF {
    /*** doc 格式 */
    private static final int DOC_FMT = 0;

    /*** docx 格式 */
    private static final int DOCX_FMT = 12;

    public static void main(String[] args) throws Exception{

//        TestPDF.READPDF("D:\\ftpfile\\11.pdf");
//        System.out.println();

//        generate(new File("D:/ftpfile/tempPrint.html"), new File("D:/ftpfile/1.doc"));

        xhtmlToDocx1("D:/ftpfile/tempPrint.html","D:/ftpfile","1.docx");



    }







    public static void xhtmlToDocx1(String xhtml, String destinationPath, String fileName)
    {
        File dir = new File (destinationPath);
        File actualFile = new File (dir, fileName);

        WordprocessingMLPackage wordMLPackage = null;
        try
        {
            wordMLPackage = WordprocessingMLPackage.createPackage();
        }
        catch (InvalidFormatException e)
        {
            e.printStackTrace();
        }


        XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

        OutputStream fos = null;
        try
        {
            fos = new ByteArrayOutputStream();
            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    XHTMLImporter.convert( xhtml, null) );

            System.out.println(XmlUtils.marshaltoString(wordMLPackage
                    .getMainDocumentPart().getJaxbElement(), true, true));

            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);
            Docx4jProperties.setProperty("docx4j.Convert.Out.HTML.OutputMethodXML",
                    true);
            Docx4J.toHTML(htmlSettings, fos, Docx4J.FLAG_EXPORT_PREFER_XSL);
            wordMLPackage.save(actualFile);
        }
        catch (Docx4JException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成文件
     * @param inputFile html文件路径
     * @param outputFile doc文件路径
     */
    public static void generate(File inputFile, File outputFile) {
        InputStream templateStream = null;
        try {
            // Get the template input stream from the application resources.
            final URL resource = inputFile.toURI().toURL();

            // Instanciate the Docx4j objects.
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

            // Load the XHTML document.
            wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(resource));

            // Save it as a DOCX document on disc.
            wordMLPackage.save(outputFile);
            // Desktop.getDesktop().open(outputFile);

        } catch (Exception e) {
            throw new RuntimeException("Error converting file " + inputFile, e);

        } finally {
            if (templateStream != null) {
                try {
                    templateStream.close();
                } catch (Exception ex) {
                 ex.printStackTrace();

                }
            }
        }
    }




//    /**
//     * 读取pdf中文字信息(全部)
//     */
//    public static void READPDF(String inputFile){
//        //创建文档对象
//        PDDocument doc =null;
//        String content="";
//        try {
//            //加载一个pdf对象
//            doc =PDDocument.load(new File(inputFile));
//            //获取一个PDFTextStripper文本剥离对象
//            PDFTextStripper textStripper =new PDFTextStripper();
//            content=textStripper.getText(doc);
////            vo.setContent(content);
//            System.out.println("内容:"+content);
//            System.out.println("全部页数"+doc.getNumberOfPages());
//            //关闭文档
//            doc.close();
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//    }
}



// 格式大全:前缀对应以下方法的fmt值
// 0:Microsoft Word 97 - 2003 文档 (.doc)
// 1:Microsoft Word 97 - 2003 模板 (.dot)
// 2:文本文档 (.txt)
// 3:文本文档 (.txt)
// 4:文本文档 (.txt)
// 5:文本文档 (.txt)
// 6:RTF 格式 (.rtf)
// 7:文本文档 (.txt)
// 8:HTML 文档 (.htm)(带文件夹)
// 9:MHTML 文档 (.mht)(单文件)
// 10:MHTML 文档 (.mht)(单文件)
// 11:XML 文档 (.xml)
// 12:Microsoft Word 文档 (.docx)
// 13:Microsoft Word 启用宏的文档 (.docm)
// 14:Microsoft Word 模板 (.dotx)
// 15:Microsoft Word 启用宏的模板 (.dotm)
// 16:Microsoft Word 文档 (.docx)
// 17:PDF 文件 (.pdf)
// 18:XPS 文档 (.xps)
// 19:XML 文档 (.xml)
// 20:XML 文档 (.xml)
// 21:XML 文档 (.xml)
// 22:XML 文档 (.xml)
// 23:OpenDocument 文本 (.odt)
// 24:WTF 文件 (.wtf)
