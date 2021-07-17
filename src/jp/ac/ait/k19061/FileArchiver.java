package jp.ac.ait.k19061;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileArchiver {
    public static void main(String[] args) {

        System.out.println("Zip圧縮するファイルを空白区切りで入力してください");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        input = input.replaceAll("^[ ]{2,}", "");   //---+
        input = input.replaceAll("[ ]{2,}", " ");   //---+--入力文字列から余分な空白を除去する
        Set<String> targets = new LinkedHashSet<>(Arrays.asList(input.split(" ", -1)));

        try {
            if (targets.size() == 1) makeZipFile(targets.iterator().next());
            else makeZipFile(targets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一つのファイルをZip圧縮する
     * @param target 対象ファイルのパス
     * @throws IOException
     */
    private static void makeZipFile(String target) throws IOException {

        Path targetPath = Path.of(target);

        //　対象が存在するか確認する
        if (!Files.exists(targetPath)) {
            System.out.println("圧縮する対象が存在しません");
            return;
        }

        // 対象が存在する
        else {
            if (Files.isDirectory(targetPath) || Files.isHidden(targetPath) || Files.isSymbolicLink(targetPath)) {
                System.out.println("対象が扱えない形式です");
                return;
            } else if (!Files.isReadable(targetPath)) {
                System.out.println("対象ファイルを読み込めません");
                return;
            } else {

                /*---Zip圧縮する---*/
                try (FileOutputStream fos = new FileOutputStream(target + ".zip");
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        ZipOutputStream zos = new ZipOutputStream(bos)) {

                    byte[] fileData = Files.readAllBytes(targetPath);
                    ZipEntry ze = new ZipEntry(target);
                    zos.putNextEntry(ze);
                    zos.write(fileData);
                } catch (IOException e) {
                    throw e;
                }

            }
        }
    }

    /**
     * 複数ファイルをまとめてZip圧縮する
     * 作成されるZipファイルの名前は「引数で渡された先頭のファイル+".zip"」
     * ディレクトリが含まれいる場合は圧縮しない
     * 隠しファイル等が含まれている場合は読み飛ばす
     * @param targets 対象ファイルのパス文字列を持ったSet
     * @throws IOException
     */
    private static void makeZipFile(Set<String> targets) throws IOException {
        String zipFileName = "";

        // ディレクトリが含まれていないか調べる
        for (Iterator<String> itr = targets.iterator();itr.hasNext();) {
            String filename = itr.next();
            if (Files.isDirectory(Path.of(filename))) {
                System.out.println("ディレクトリは圧縮対象にできません");
                return;
            }

            if (zipFileName.equals("")) zipFileName = filename + ".zip";
        }

        /*---Zip圧縮する---*/
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ZipOutputStream zos = new ZipOutputStream(bos)) {

            for(Iterator<String> itr = targets.iterator();itr.hasNext();) {
                String filename = itr.next();
                Path targetPath = Path.of(filename);

                if (Files.exists(targetPath) && Files.isReadable(targetPath)) {
                    byte[] fileData = Files.readAllBytes(targetPath);
                    ZipEntry ze = new ZipEntry(filename);
                    zos.putNextEntry(ze);
                    zos.write(fileData);
                }
            }

        } catch (IOException e) {
            throw e;
        }

    }
}
