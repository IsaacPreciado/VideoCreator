import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class Utility {
    public static ArrayList Order(ArrayList dates, ArrayList ubi, ArrayList fileNames) {
        ArrayList orderedFiles = new ArrayList();
        int cant = dates.size();
        String[] newDates = new String[cant];
        ArrayList newFiles = new ArrayList();
        ArrayList newUbi = new ArrayList();

        for (int i = 0; i < cant; i++) {
            orderedFiles.add(dates.get(i).toString() + "||" + ubi.get(i).toString() + "||" + fileNames.get(i).toString());
            newDates[i] = dates.get(i).toString();
        }
        Collections.sort(orderedFiles);
        Collections.sort(dates);

        for (int i = 0; i < cant; i++) {
            for (int j = 0; j < cant; j++) {
                if (dates.get(i).toString().equals(newDates[j])) {

                    newFiles.add(fileNames.get(j));
                    newUbi.add(ubi.get(j));
                    break;
                }
            }

        }

        for (int i = 0; i < cant; i++) {
            fileNames.set(i, newFiles.get(i));
            ubi.set(i, newUbi.get(i));
        }

        return orderedFiles;
    }

    public static ArrayList RemoveExtension(ArrayList files) {
        String name;
        for (int i = 0; i < files.size(); i++) {
            name = "";
            for (int j = 0; files.get(i).toString().charAt(j) != '.'; j++) {
                name += files.get(i).toString().charAt(j);
            }
            files.set(i, name);

        }
        return files;
    }

    public static boolean haveExtensions(String line) {
        String aux = "";
        boolean isIn = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '.' || isIn) {
                isIn = true;
                aux += line.charAt(i);
            }

        }
        line = aux;

        if (line.equals(".mov") || line.equals(".jpg") || line.equals(".png") || line.equals(".heic") || line.equals(".mp4")) {
            return true;
        } else {
            return false;
        }

    }

    public static ArrayList toDegrees(ArrayList ubi) {
        //(latitude, longitude)
        String latitude, longitude;
        boolean pass;

        for(int i = 0; i < ubi.size(); i++) {
            latitude = "";
            longitude = "";
            pass = false;
            for (int j = 0; j < ubi.get(i).toString().length(); j++) {
                if (ubi.get(i).toString().charAt(j) == ',') {
                    pass = true;
                    j += 2;
                }
                if (!pass) {
                    latitude += ubi.get(i).toString().charAt(j);
                } else{
                    longitude += ubi.get(i).toString().charAt(j);
                }

            }
            ubi.set(i, conversion(latitude) + " " + conversion(longitude));
        }

        return ubi;
    }

    private static float conversion(String coordenate){
        int sign;
        char position;
        String[] coordenates = coordenate.split("\\s");
        String[] values = {"", "", ""};
        float grades;
        DecimalFormat df = new DecimalFormat("0.00");

        values[0] = coordenates[0];
        for(int i = 0; coordenates[2].charAt(i) != '\'';i++){
            values[1] += coordenates[2].charAt(i);
        }
        for(int i = 0; coordenates[3].charAt(i) != '"';i++){
            values[2] += coordenates[3].charAt(i);
        }

        position = coordenate.charAt(coordenate.length() - 1);
        if(position == 'N' || position == 'E'){
            sign = 1;
        }else{
            sign = -1;
        }

        grades = Float.parseFloat(df.format(Double.parseDouble(values[0]) + Double.parseDouble(values[1])/60.0 + Double.parseDouble(values[2])/3600.0))*sign;

        return grades;
    }


    public static void addTemp(String[] newFiles, String[] temperature){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            for(int i = 0; i < newFiles.length - 1; i++){
                processBuilder.command("ffmpeg", "-i", newFiles[i], "-vf", "drawtext=text='"+ temperature[i]+"°':fontsize=200", "text" + newFiles[i]).start();
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    System.out.println(e);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createVideo(String[] newFiles, ArrayList fileNames){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            String fileName;
            for(int i = 0; i < newFiles.length; i++){
                fileName = newFiles[i];
                    processBuilder.command("ffmpeg", "-loop", "1", "-i", "text" + fileName,"-t", "00:00:03", "-acodec","copy", "-vcodec", "libx264", "text" + fileNames.get(i).toString() + ".mp4").start();
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
            //creating video of the map
            int time = fileNames.size() + 3;
            processBuilder.command("ffmpeg", "-loop", "1", "-i", "finalMapImg.jpg","-t", "00:00:" + time, "-acodec","copy", "-vcodec", "libx264","finalMap.mp4").start();

            try {
                Thread.sleep(fileNames.size()*3000);
            }catch (Exception e){
                System.out.println(e);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void createFiletxt(ArrayList fileNames) throws IOException {
        File file = new File("files/archivo.txt");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("file 'IAvideo.mp4'\n");
        for(int i = 0; i < fileNames.size()-1; i++){
            bw.write("file " + "'text" + fileNames.get(i).toString() + ".mp4'\n");
        }
        bw.write("file " + "'finalMap.mp4'");
        bw.close();

    }
}