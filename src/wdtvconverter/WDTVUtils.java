package wdtvconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author camon
 */
public class WDTVUtils {
    private WDTVUtils() {};

    public static long convert(File source,
            File destination, Configuration conf, JTextArea output)
            throws IOException {

        long start = System.currentTimeMillis();

        List<File> toDelete = new LinkedList<File>();
        
        int extIndex = source.getName().lastIndexOf(".");

        String ext = (extIndex >= 0) ?
            source.getName().substring(extIndex + 1) : "";

        if (ext.compareToIgnoreCase("avi") == 0) {
            String path = (new File(
            WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
            "\\mkvtoolnix\\mkvmerge.exe").replace("%20", " ");

            File outFile = new File(Constants.tempDir, "tmp1.mkv");
            String cmd = path + " -o " + "\"" + outFile.getAbsolutePath() +
                            "\"" + " " + "\"" + source.getAbsolutePath() + "\"";

            //System.out.println(cmd);

            Process proc = WDTVConverterApp.APP_RUNTIME.exec(cmd);
            InputStream inputstream = proc.getInputStream();
            InputStreamReader inputstreamreader =
                    new InputStreamReader(inputstream);
            BufferedReader bufferedreader =
                    new BufferedReader(inputstreamreader);

            // read the ls output

            String step0 = "Step 0/4 (Converting AVI to MKV)\n";
            String mkvmergeOut = "";
            String progress = "";

            String line = null;
            while ((line = bufferedreader.readLine()) != null) {
                line = line.trim();
                if (line.length() <= 0) continue;
                if (line.contains("Progress")) progress = line + "\n";
                else mkvmergeOut += line + "\n";

                output.setText(step0 + mkvmergeOut + progress);
            }

            // check for ls failure

            try {
                if (proc.waitFor() != 0) {
                    System.err.println("exit value = " + proc.exitValue());
                } else {
                    source = outFile;
                    toDelete.add(outFile);
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        String mkvinfoOut = "";
        String mkvextractOut = "";
        String eac3toOut = "";
        String mkvmergeOut = "";
        String step1 = "Step 1/4 (Retrieving File Info)\n";
        String step2 = "Step 2/4 (Extracting DTS tracks)\n";
        String step3 = "Step 3/4 (Encoding DTS to AC3)\n";
        String step4 = "Step 4/4 (Remuxing files to mkv)\n";
        String progress = "";

        String cmd = null;
        String path = (new File(
                WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
                "\\mkvtoolnix\\mkvinfo.exe").replace("%20", " ");

        cmd = path + " " + "\"" + source.getAbsolutePath() + "\"";
        //System.out.println(cmd);
        
        Process proc = WDTVConverterApp.APP_RUNTIME.exec(cmd);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader =
                    new InputStreamReader(inputstream);
        BufferedReader bufferedreader =
                    new BufferedReader(inputstreamreader);

        // read the ls output

        mkvinfoOut += source.getName() + "\n";
        String line;
        Node root = new Node(source.getName(), null);
        Node current = root;
        int lastLevel = -1;
        while ((line = bufferedreader.readLine()) != null) {
            line = line.trim();
            if (line.length() <= 0) continue;
            mkvinfoOut += line + "\n";
            int level = getLevel(line);
            String rep = line.replace("+","").replace("|","");
            int i = rep.indexOf(":");
            if (i < 0) i = rep.indexOf(",");
            String key = null;
            String value = null;
            if (i < 0) key = rep.trim();
            else {
                key = rep.substring(0, i).trim();
                value = rep.substring(i + 1).trim();
            }
            Node newNode = new Node(key, value);
            if (level > lastLevel) {
                current.add(newNode);
                current = newNode;
            } else if (level < lastLevel) {
                int down = lastLevel - level + 1;
                Node node = current;
                while (down-- > 0) node = node.getParent();
                node.add(newNode);
                current = newNode;
            } else {
                Node parent = current.getParent();
                parent.add(newNode);
                current = newNode;
            }
            lastLevel = level;

            output.setText(step1 + mkvinfoOut);
        }

        // check for ls failure

        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }

        List<Node> tracks = root.get("Segment").get(0)
                .get("Segment tracks").get(0).get("A track");
        Node video = null;
        List<Node> audios = new LinkedList<Node>();
        List<Node> subs = new LinkedList<Node>();
        for (Node n : tracks) {
            String type = n.get("Track type").get(0).getValue();
            if (type.equals("video")) video = n;
            else if (type.equals("audio")) audios.add(n);
            else if (type.equals("subtitles")) subs.add(n);
        }

        //extract all dts tracks
        String args = "tracks " + "\"" + source.getAbsolutePath() + "\"";
        for (Node a : audios) {
            String codec = a.get("Codec ID").get(0).getValue();
            if (codec.equals("A_DTS")) {
                String id = a.get("Track number").get(0).getValue();
                args += " " + id + ":" + "\"" + 
                    new File(Constants.tempDir,id + ".dts").getAbsolutePath() +
                    "\"";
            }
        }

            
        path = (new File(
            WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
            "\\mkvtoolnix\\mkvextract.exe").replace("%20", " ");

        cmd = path + " " + args;
        //System.out.println(cmd);

        proc = WDTVConverterApp.APP_RUNTIME.exec(cmd);
        inputstream = proc.getInputStream();
        inputstreamreader = new InputStreamReader(inputstream);
        bufferedreader = new BufferedReader(inputstreamreader);

        // read the ls output

        line = null;
        progress = "";
        while ((line = bufferedreader.readLine()) != null) {
            line = line.trim();
            if (line.length() <= 0) continue;
            if (line.contains("Progress")) progress = line + "\n";
            else mkvextractOut += line + "\n";

            output.setText(step2 + mkvextractOut + progress);
        }

        // check for ls failure

        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }

        for (Node a : audios) {
            String codec = a.get("Codec ID").get(0).getValue();
            if (codec.equals("A_DTS")) {
                String id = a.get("Track number").get(0).getValue();
                args = "\"" + new File(Constants.tempDir,
                        id + ".dts").getAbsolutePath() + "\"" + " " +
                        "\"" + new File(Constants.tempDir,
                        id + ".ac3").getAbsolutePath() + "\"" + " -" +
                        conf.getBitRate() + " -libav";

                path = (new File(
                    WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
                        "\\eac3to\\eac3to.exe").replace("%20", " ");

                cmd = path + " " + args;
                //System.out.println(cmd);

                proc = WDTVConverterApp.APP_RUNTIME.exec(cmd);
                inputstream = proc.getInputStream();
                inputstreamreader = new InputStreamReader(inputstream);
                bufferedreader = new BufferedReader(inputstreamreader);

                // read the ls output

                line = null;
                progress = "";
                while ((line = bufferedreader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() <= 0) continue;
                    if (line.contains("Progress")) progress = line + "\n";
                    else eac3toOut += line + "\n";

                    output.setText(step3 + eac3toOut + progress);
                }

                // check for ls failure

                try {
                    if (proc.waitFor() != 0) {
                        System.err.println("exit value = " +
                                                proc.exitValue());
                    }
                } catch (InterruptedException e) {
                    System.err.println(e);
                }

                new File(Constants.tempDir, id + ".dts").delete();
            }
        }

        String out = "-o " + "\"" + destination.getAbsolutePath() + "\"";
        String copy = " -a ";
        String dts = "";
        int copyCount = 0;
        for (Node a : audios) {
            String codec = a.get("Codec ID").get(0).getValue();
            String id = a.get("Track number").get(0).getValue();
            if (codec.equals("A_DTS")) {
                int flag = Integer.valueOf(
                        a.get("Default flag").get(0).getValue());
                String lang = a.get("Language").get(0).getValue();
                dts += (flag == 1 ? " --default-track 0 " : " ") +
                        "--language 0:" + lang + " -a 0 " + "\"" +
                        new File(Constants.tempDir,
                        id + ".ac3").getAbsolutePath() + "\"";
                toDelete.add(new File(Constants.tempDir, id + ".ac3"));
            } else {
                copy += id + ",";
                copyCount++;
            }
        }
        if (copyCount == 0) copy = " -A";
        else copy = copy.substring(0, copy.length() - 1);

        String subtitles = "";

        if (conf.hasSubtitles()) {
            Collection<Subtitle> subsc = conf.getSubtitles();
            for (Subtitle sub : subsc) {
                if (sub.isDefault()) subtitles += "--default-track 0 ";
                subtitles += "--language 0:" +
                        sub.getLanguage().getISO6392code() +
                        " " + "\"" + sub.getFile().getAbsolutePath() +
                        "\"" + " ";
            }
        }
        subtitles = subtitles.trim();

        path = (new File(
            WDTVConverterApp.APP_LOCATION.getFile()).getParent() +
            "\\mkvtoolnix\\mkvmerge.exe").replace("%20", " ");

        cmd = path + " " + out + copy + " " + "\"" +
                source.getAbsolutePath() + "\"" + dts + " " + subtitles;

        //System.out.println(cmd);

        proc = WDTVConverterApp.APP_RUNTIME.exec(cmd);
        inputstream = proc.getInputStream();
        inputstreamreader =
                new InputStreamReader(inputstream);
        bufferedreader =
                new BufferedReader(inputstreamreader);

        // read the ls output

        line = null;
        while ((line = bufferedreader.readLine()) != null) {
            line = line.trim();
            if (line.length() <= 0) continue;
            if (line.contains("Progress")) progress = line + "\n";
            else mkvmergeOut += line + "\n";

            output.setText(step4 + mkvmergeOut + progress);
        }

        // check for ls failure

        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }

        for (File f : toDelete) {
            f.delete();
            String parent = f.getParentFile().getAbsolutePath();
            String name = f.getName();
            int index = name.lastIndexOf(".");
            String fileExt = (index >= 0) ? name.substring(index + 1) : "";
            if (fileExt.compareToIgnoreCase("ac3") == 0) {
                if (index >= 0) name = name.substring(0, index);
                new File(parent, name + " - Log.txt").delete();
            }
        }

        long end = System.currentTimeMillis();

        output.setText("Completed in " + ((end - start)/1000.0) + "s");

        return (end - start);
    }

    private static int getLevel(String line) {
        int index = line.indexOf("+");
        if (index < 0) return -1;
        String ls = line.substring(0, index + 1);
        if (!ls.contains("|")) return 0;
        else {
            String blanks = ls.substring(1, ls.length() - 1);
            return blanks.length() + 1;
        }
    }
}