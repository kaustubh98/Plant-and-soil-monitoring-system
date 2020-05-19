package com.example.plantmonitoringsystem.SupportClasses;

import android.util.Log;

import com.example.plantmonitoringsystem.Fragments.FragmentAverage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.ContentValues.TAG;

public class CreatePDF {

    private String path;
    private PdfPTable AverageDetailsTable = new PdfPTable(2);
    private String units = "-1";

    public CreatePDF(String path) {
        this.path = path;
    }

    public File getFile() throws IOException, DocumentException {
        File file = new File(path);
        OutputStream stream = new FileOutputStream(file);

        //add required content in the PDF file
        Document document = new Document();
        PdfWriter.getInstance(document,stream );
        document.open();
        document.setPageSize(PageSize.A4);
        BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", BaseFont.EMBEDDED);

        //add heading
        Font mHeadingFont = new Font(baseFont,36.0f,Font.BOLD, BaseColor.BLACK);
        Chunk mHeadingChunk = new Chunk(FragmentAverage.getName()+"'s Farm",mHeadingFont);
        Paragraph mHeading = new Paragraph(mHeadingChunk);
        mHeading.setAlignment(Element.ALIGN_CENTER);
        document.add(mHeading);

        //adding the line separator
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(BaseColor.GRAY);
        lineSeparator.setLineWidth(0.5f);
        document.add(new Paragraph(" "));
        document.add(lineSeparator);

        //add farm description
        Font mFarmDescriptionFont = new Font(baseFont,25.0f,Font.NORMAL);
        Chunk mFarmDescriptionChunk = new Chunk("This farm is covered by "+FragmentAverage.getNumberOfUnits()+" units. These deployed units cover various sections of the farm. " +
                "The table below shows the average of the data received from these units.",mFarmDescriptionFont);
        Paragraph mFarmDescription = new Paragraph(mFarmDescriptionChunk);
        document.add(mFarmDescription);
        document.add(new Paragraph(" "));

        //adding data in tabular form
        insertCell("Parameter",new Font(baseFont,25.0f,Font.BOLD));
        insertCell("Value",new Font(baseFont,25.0f,Font.BOLD));
        insertCell("Temperature",new Font(baseFont,25.0f,Font.NORMAL));
        insertCell(FragmentAverage.getTemp(),new Font(baseFont,25.0f,Font.NORMAL));
        insertCell("Hunidity",new Font(baseFont,25.0f,Font.NORMAL));
        insertCell(FragmentAverage.getHumidity(),new Font(baseFont,25.0f,Font.NORMAL));
        insertCell("Soil Moisture",new Font(baseFont,25.0f,Font.NORMAL));
        insertCell(FragmentAverage.getMoisture(),new Font(baseFont,25.0f,Font.NORMAL));
        insertCell("Light Intensity",new Font(baseFont,25.0f,Font.NORMAL));
        insertCell(FragmentAverage.getLight(),new Font(baseFont,25.0f,Font.NORMAL));
        document.add(AverageDetailsTable);

        document.close();
        stream.close();
        return file;
    }


    private void insertCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(),font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        AverageDetailsTable.addCell(cell);
    }
}
