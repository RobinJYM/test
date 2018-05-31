
import java.io.File;
import java.io.IOException;
import java.sql.*;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
public class scraper {
    private static int depth = 1;


    public static void printPdf(String title, String path1){
        String content = null;
        try{
            PdfReader reader = new PdfReader(path1);
            int pageNum = reader.getNumberOfPages();
            for (int i =1;i<=pageNum;i++){
                content +=PdfTextExtractor.getTextFromPage(reader, i);
            }
            writeToDB(title, content);
            //System.out.println(content);

        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void find(String pathName, int depth) throws IOException {
        int filecount = 0;
        //create file object based on given pathname
        File dirFile = new File(pathName);

        if (!dirFile.exists()) {
            System.out.println("do not exit");
            return;
        }
        //if it is not a directory, and it is a file, then print out
        if (!dirFile.isDirectory()) {
            if (dirFile.isFile()) {

                System.out.println(dirFile.getCanonicalFile());
            }
            return;
        }

        for (int j = 0; j < depth; j++) {
            System.out.print("  ");
        }
        System.out.print("|--");
        System.out.println(dirFile.getName());
        //fetch all files and directories names below such directory
        String[] fileList = dirFile.list();
        int currentDepth = depth + 1;
        for (int i = 0; i < fileList.length; i++) {
            //iterate all the directory
            String string = fileList[i];
            //Creates a new File instance from a parent abstract pathname and a child pathname string.
            File file = new File(dirFile.getPath(), string);

            String name = file.getName();
            //if it is a directory, depth++,print out directory name and recursively call Find method.
            if (file.isDirectory()) {
                //recursion
                find(file.getCanonicalPath(), currentDepth);
            } else {
                //if it is a file, directly print out file name;
                for (int j = 0; j < currentDepth; j++) {
                    System.out.print("   ");
                }
                System.out.print("|--");
                System.out.println(name);
                String path = file.getPath();

                printPdf(name, path);
                //                try{
//                    PDDocument document = null;
//                    File pdffile = new File(dirFile.getPath());
//                    document=PDDocument.load(pdffile);
//
//                    // fetch pages
//                    int pages = document.getNumberOfPages();
//
//                    // read content
//                    PDFTextStripper stripper=new PDFTextStripper();
//                    // print in order
//                    stripper.setSortByPosition(true);
//                    stripper.setStartPage(1);
//                    stripper.setEndPage(pages);
//                    String content = stripper.getText(document);
//                    System.out.println(content);
//                }
//                catch (Exception e){
//                    System.out.println(e);
//                }
            }
        }
    }
    public static void writeToDB(String title,String content){
        Connection conn = null;
        String dbName = "TutorialDB";
        String serverip="127.0.0.1";
        String serverport="1433";
        String url = "jdbc:sqlserver://"+serverip+"\\SQLEXPRESS:"+serverport+";databaseName="+dbName+"";
        Statement stmt = null;
        ResultSet result = null;
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String databaseUserName = "sa";
        String databasePassword = "admin";
        try {
            //Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, databaseUserName, databasePassword);
            String sql = "INSERT INTO dbo.nhm (title, filecontent) VALUES(?,?)";
            PreparedStatement pstmt= conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.executeUpdate();

            pstmt.close();
//            stmt = conn.createStatement();
//            result = stmt.executeQuery("Select * From dbo.nhm ");
//            while (result.next()) {
//                title=result.getString("filecontent");
//                System.out.println(title);
//            }
//            result = null;
//            String pa,us;

//            while (result.next()) {
//                us=result.getString("CustomerId");
//                pa = result.getString("Name");
//                System.out.println(us+"  "+pa);
//            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        //connectDB();
        find("D:\\sandbox", depth);

    }
}