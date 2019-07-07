package com.zzw.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
//辅助VirtualFile
public class VirtualFileUtil {
    public static Document getDocument(VirtualFile file) {
        if (file == null || (!file.exists())) {
            return null;
        }
        return FileDocumentManager.getInstance().getDocument(file);
    }

    public static VirtualFile getFile(Document document) {
        if (document == null) {
            return null;
        }
        return FileDocumentManager.getInstance().getFile(document);
    }

    public static String getPath(VirtualFile file) {
        boolean exist = (file == null) ? false : file.exists();
        String path = exist ? file.getPath() : null;
        return (path == null) ? path : StringUtil.md5Encode(path);
    }

    public static long getFileLength(VirtualFile file) {
        boolean exist = (file == null) ? false : file.exists();
        long fileLen = exist ? file.getLength() : -1;
        return fileLen;
    }

    public static String getFileLengthString(VirtualFile file) {
        long fileLen = getFileLength(file);
        String fileLenStr = (fileLen == -1) ? null : String.valueOf(fileLen);
        return fileLenStr;
    }

    public static int getDocumentLength(Document document) {
        int docLen = (document == null) ? -1 : document.getTextLength();
        return docLen;
    }

    public static String getDocumentLengthString(Document document) {
        int docLen = getDocumentLength(document);
        String docLenStr = (docLen == -1) ? null : String.valueOf(docLen);
        return docLenStr;
    }

    public static int getDocumentLine(Document document) {
        int line = (document == null) ? -1 : document.getLineCount();
        return line;
    }

    public static String getDocumentLineString(Document document) {
        int line = getDocumentLine(document);
        String lineStr = (line == -1) ? null : String.valueOf(line);
        return lineStr;
    }
}
