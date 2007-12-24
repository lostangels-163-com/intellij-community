package com.intellij.codeInsight.editorActions;

import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.util.text.StringUtil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TextBlockTransferable implements Transferable {
  private final Collection<TextBlockTransferableData> myExtraData;
  private final RawText myRawText;
  private final String myText;
  private final DataFlavor[] myTransferDataFlavors;

  public TextBlockTransferable(String text, Collection<TextBlockTransferableData> extraData, RawText rawText) {
    myText = text;
    myExtraData = extraData;
    myRawText = rawText;

    List<DataFlavor> dataFlavors = new ArrayList<DataFlavor>();
    Collections.addAll(dataFlavors, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor, RawText.FLAVOR);
    for(TextBlockTransferableData data: extraData) {
      dataFlavors.add(data.getFlavor());
    }
    myTransferDataFlavors = dataFlavors.toArray(new DataFlavor[dataFlavors.size()]);
  }

  public DataFlavor[] getTransferDataFlavors() {
    return myTransferDataFlavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    DataFlavor[] flavors = getTransferDataFlavors();
    for (DataFlavor flavor1 : flavors) {
      if (flavor.equals(flavor1)) {
        return true;
      }
    }
    return false;
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    for(TextBlockTransferableData data: myExtraData) {
      if (data.getFlavor().equals(flavor)) {
        return data;
      }
    }
    if (RawText.FLAVOR.equals(flavor)) {
      return myRawText;
    }
    else if (DataFlavor.stringFlavor.equals(flavor)) {
      return myText;
    }
    else if (DataFlavor.plainTextFlavor.equals(flavor)) {
      return new StringReader(myText);
    }
    else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  public static String convertLineSeparators(String text,
                                             String newSeparator,
                                             Collection<TextBlockTransferableData> transferableDatas) {
    if (transferableDatas.size() > 0){
      int size = 0;
      for(TextBlockTransferableData data: transferableDatas) {
        size += data.getOffsetCount();
      }

      int[] offsets = new int[size];
      int index = 0;
      for(TextBlockTransferableData data: transferableDatas) {
        index = data.getOffsets(offsets, index);
      }

      text = StringUtil.convertLineSeparators(text, newSeparator, offsets);

      index = 0;
      for(TextBlockTransferableData data: transferableDatas) {
        index = data.setOffsets(offsets, index);
      }

      return text;
    }
    else{
      return StringUtil.convertLineSeparators(text, newSeparator);
    }
  }
}