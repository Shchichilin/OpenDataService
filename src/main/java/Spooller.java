import java.io.*;
public class Spooller implements Runnable
{
    private InputStream in;
    private OutputStream out;
    private Reader reader;
    private Writer writer;
    private Type type;
    private int bufferSize = 16 * 1024;
    public Spooller(InputStream in, OutputStream out)
    {
        if(in == null || out == null)
        {
            throw new NullPointerException();
        }
        this.in = in;
        this.out = out;
        type = Type.BINRY;
    }
    public Spooller(Reader reader, Writer writer)
    {
        if(reader == null || writer == null)
        {
            throw new NullPointerException();
        }
        this.reader = reader;
        this.writer = writer;
        type = Type.CHARACTER;
    }
    public int getBufferSize()
    {
        return bufferSize;
    }
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }
    public Type getType()
    {
        return type;
    }
    public void run()
    {
        switch(type)
        {
            case BINRY:
                spoolBinary();
                break;
            case CHARACTER:
                spoolCharacter();
                break;
        }
    }
    private void spoolBinary()
    {
        try
        {
            byte[] buffer = new byte[bufferSize];
            int read;
            while((read = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(in);
            close(out);
        }
    }
    public void spool()
    {
        Thread spoolThread = new Thread(this, "Spool thread");
        spoolThread.start();
    }
    private void spoolCharacter()
    {
        try
        {
            char[] buffer = new char[bufferSize];
            int read;
            while((read = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, read);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close(reader);
            close(writer);
        }
    }
    private static void close(Closeable closeable)
    {
        if(closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch(Exception e)
            {
                //ignore
            }
        }
    }
    public static enum Type
    {
        BINRY,
        CHARACTER
    }
}