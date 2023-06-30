import java.io.*;

public class API {
    public static String Temperature(String latitude, String longitude) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Process process = processBuilder.directory(new File("files/")).command("curl", "-X", "POST", "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=aab75ecff6d49a1079eb542c42371a94&units=metric").start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String[] arrayTemp = reader.readLine().split("temp");
            String temp = arrayTemp[1], finalTemp = "";
            for (int i = 2; temp.charAt(i) != ','; i++) {
                finalTemp += temp.charAt(i);
            }


            return finalTemp;

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public static void mapQuestImg(String[] initialCoordinates, String[] finalCoordinates) {
        try {

            //generate AI phrase
            String phrase = AIphrase();

            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            if (initialCoordinates[0].equals(finalCoordinates[0]) && initialCoordinates[1].equals(finalCoordinates[1])) {
                processBuilder.command("curl", "-o", "finalMap.jpg", "https://www.mapquestapi.com/staticmap/v5/map?locations=" + initialCoordinates[0] + "," + initialCoordinates[1] + "&size=@2x&key=qrQa0tTEfRmdjn0cPzqzzThRZn9iWNKf").start();
            } else {
                processBuilder.command("wget", "-O", "finalMap.jpg", "https://www.mapquestapi.com/staticmap/v5/map?start=" + initialCoordinates[0] + "," + initialCoordinates[1] + "&end=" + finalCoordinates[0] + "," + finalCoordinates[1] + "&routeArc=true&size=600,400@2x&key=qrQa0tTEfRmdjn0cPzqzzThRZn9iWNKf").start();
            }

            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e);
            }

            //adding AI phrase to the map image
            processBuilder.command("ffmpeg", "-i", "finalMap.jpg", "-vf", "drawtext=text='" + phrase + "':fontsize=20", "finalMapImg.jpg").start();
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AIimage() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            Process process = processBuilder.command("sh", "-c", "curl https://api.openai.com/v1/images/generations \\\n" +
                    "  -H \"Content-Type: application/json\" \\\n" +
                    "  -H \"Authorization: Bearer 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\" \\\n" +
                    "  -d '{\n" +
                    "    \"prompt\": \"Vacaciones\",\n" +
                    "    \"n\": 1,\n" +
                    "    \"size\": \"1024x1024\"\n" +
                    "  }'").start();
            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line, ans = "";
            String[] link;
            int cont = 0;

            while ((line = reader.readLine()) != null) {
                ans += line;
            }

            link = ans.split("url\": \"");
            ans = "";
            while (link[1].charAt(cont) != '\"') {
                ans += link[1].charAt(cont);
                cont++;
            }

            processBuilder.command("curl", "-o", "IAimg.jpg", ans).start();
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e);
            }
            //creating video with the map image
            processBuilder.command("ffmpeg", "-r", "1/3", "-i", "IAimg.jpg", "-pix_fmt", "yuv420p", "IAvideo.mp4").start();

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String AIphrase() {
        try {

            ProcessBuilder processBuilder = new ProcessBuilder().directory(new File("files/"));
            Process process = processBuilder.command("sh", "-c", "curl https://api.openai.com/v1/chat/completions \\\n" +
                    "  -H \"Content-Type: application/json\" \\\n" +
                    "  -H \"Authorization: Bearer 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\" \\\n" +
                    "  -d '{\n" +
                    "    \"model\": \"gpt-3.5-turbo\",\n" +
                    "    \"messages\": [{\"role\": \"user\", \"content\": \"Inspirational short phrase of vacations\"}]\n" +
                    "  }'").start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line, ans = "";
            String[] link;
            int cont = 1;

            while ((line = reader.readLine()) != null) {
                ans += line;
            }

            link = ans.split("content\":\"");
            ans = "";
            while (link[1].charAt(cont) != '.') {
                ans += link[1].charAt(cont);
                cont++;
            }
            ans += '\"';
            return ans;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}