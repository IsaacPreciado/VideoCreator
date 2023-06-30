import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            //ADD THE FILES IN "files"
            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            Process process = processBuilder.command("ls", "-la").start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            ArrayList dates = new ArrayList();
            ArrayList ubi = new ArrayList();
            ArrayList fileNames = new ArrayList();
            String line, fileName;
            int cont;

            //create the IAimage with the openai api
            API.AIimage();

            //extract the file names
            while((line = reader.readLine())!= null) {
                if(Utility.haveExtensions(line)){
                    cont = 0;
                    for(int i = line.length() - 1; line.charAt(i) != ' '; i--){
                        cont++;
                    }
                    fileName = line.substring(line.length() - cont);

                    fileNames.add(fileName);
                }
            }

            int cant = fileNames.size();

            //add ubication and creation date of the files
            for(int i = 0; i <  cant; i++){
                //these functions receive the name of the files to extract the ubication and creation date.
                //Then add it to an arraylist
                ubi.add(Info.Ubication(fileNames.get(i).toString()));
                dates.add(Info.Dates(fileNames.get(i).toString()));
            }

            //receive dates, ubication and filenames to order each one
            Utility.Order(dates, ubi, fileNames);

            //change the coordinates to degrees
            Utility.toDegrees(ubi);
            //taking initial and final coordinates to create the map
            String[] initialCoordinates = ubi.get(0).toString().split(" ");
            String[] finalCoordinates = ubi.get(ubi.size()-1).toString().split(" ");

            //this function receive the initial coordinates and the final coordinates to create the map. Sending to the mapQuest api to create the map
            API.mapQuestImg(initialCoordinates, finalCoordinates);
            fileNames.add("finalMapImg.jpg");
            cant++;


            //save the file names in a new array
            String[] newFiles = new String[cant];
            for(int i = 0; i  < cant; i++){
                newFiles[i] = fileNames.get(i).toString();
            }

            //get latitude y longitude and saving it in the temperature array
            String[] grades, temperature = new String[cant];
            String latitude, longitude;
            for(int i = 0; i < cant - 1; i++){
                grades = ubi.get(i).toString().split(" ");
                latitude = grades[0];
                longitude = grades[1];
                temperature[i] = API.Temperature(latitude, longitude);
            }

            //this function receive the name of the file and the temperature to add the temperature to the image
            Utility.addTemp(newFiles, temperature);

            //remove extension for all the files
            Utility.RemoveExtension(fileNames);
            //receive info to create a 3 seconds video from each file if is an image
            Utility.createVideo(newFiles, fileNames);

            //create the txt file to save the name of the files
            Utility.createFiletxt(fileNames);

            try {
                Thread.sleep(fileNames.size()*3000);
            }catch (Exception e){
                System.out.println(e);
            }

            //creating final video called "finalVideo.mp4"
            processBuilder.command("ffmpeg", "-f", "concat", "-i", "archivo.txt", "-c", "copy", "finalVideo.mp4").start();

            try {
                Thread.sleep(3000);
            }catch (Exception e){
                System.out.println(e);
            }

        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}