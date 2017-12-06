import java.io.File;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;

public class Test
{
    public static final String PROGRAMM = "C:\\OpenDataService\\OpenDataService.exe";
    public static void main(String[] args) throws Exception
    {
        //new File(PROGRAMM).getCanonicalPath();
        File workDir = new File(PROGRAMM).getParentFile();
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C",PROGRAMM+"/console" );
        processBuilder.directory(workDir);
        Process process = processBuilder.start();
        new Spooller(new InputStreamReader(process.getInputStream(), "windows-1251"), new OutputStreamWriter(System.out, "windows-1251")).spool();
        new Spooller(new InputStreamReader(process.getErrorStream(), "windows-1251"), new OutputStreamWriter(System.err, "windows-1251")).spool();
        int result = process.waitFor();
        System.out.println("Process finished. Exit code 111 " + result);
    }
}