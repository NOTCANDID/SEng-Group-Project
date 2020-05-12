import java.io.File;
public class FileDelete
{
    public static void fileDelete(String fileName)
    {
        try
        {
            File f= new File(fileName);           //file to be delete
            if(f.delete())                      //returns Boolean value
            {
            }
            else
            {
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}  