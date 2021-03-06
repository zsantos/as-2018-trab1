/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CollectEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author kanto
 */
public class FileParser {

    //Vars
    private File file;
    private ArrayList<Message> info;

    public FileParser(String s) {
        file = new File(s);
        info = new ArrayList<>();
    }

    //Reads data into info
    public void readFile() throws FileNotFoundException {
        //Separated by comma
        Scanner sc = new Scanner(file).useDelimiter(";");
        while (sc.hasNextLine()) {
            info.add(new Message(sc.nextLine()));
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ArrayList<Message> getInfo() {
        return info;
    }

    public void setInfo(ArrayList<Message> info) {
        this.info = info;
    }
    
    //Main for test
    /*public static void main(String [] args) throws FileNotFoundException{
        FileParser p = new FileParser("src/Data/HB.txt");
        p.readFile();
        ArrayList<String> x = p.info.getMessages();
        for(String s : x){
            System.out.println(s);
        }
    }*/
}
