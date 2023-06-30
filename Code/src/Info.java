import java.io.*;

public class Info {
    public static String Ubication(String file) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();

            Process process = processBuilder.directory(new File("files/")).command("exiftool", "-GPSPosition",file).start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;
            String ubi = "";

            while ((line = reader.readLine()) != null) {
                ubi = line.substring(34, line.length());
            }
            return ubi;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String Dates(String file) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Process process = processBuilder.directory(new File("files/")).command("exiftool", "-CreateDate",file).start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;
            String dates = "";

            while ((line = reader.readLine()) != null) {
                dates = line.substring(34, 53);
            }

            return dates;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}