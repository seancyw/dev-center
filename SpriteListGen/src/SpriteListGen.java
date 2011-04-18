
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class Resource {

    protected String path;
    protected String id;

    public Resource(String path, String id) {
        this.path = path;
        this.id = id;
    }
}

public class SpriteListGen {

    public static void main(String[] args) {
        String cmd = "-core e:\\__Projects\\CowboysAndAliens\\W810i\\data\\triplet\\data.core";
        args = cmd.split(" ");
        try {
            String coreFile = null;
            String listFile = null;
            String tabName = null;
                
            for (int i = 0; i < args.length; i++) {
                if ((args[i].compareToIgnoreCase("-core") == 0) && (i + 1 < args.length)) {
                    coreFile = args[i + 1];
                    System.out.println("-core " + args[i + 1]);
                } else if ((args[i].compareToIgnoreCase("-tabname") == 0) && (i + 1 < args.length)) {
                    tabName = args[i + 1];
                    System.out.println("-tabname " + args[i + 1]);
                } else if ((args[i].compareToIgnoreCase("-list") == 0) && (i + 1 < args.length)) {
                    listFile = args[i + 1];
                    System.out.println("-list " + args[i + 1]);
                }
            }

            Resource[]              resourcesList           = null;
            DocumentBuilderFactory  factory                 = DocumentBuilderFactory.newInstance();
            DocumentBuilder         builder;
            builder                                         = factory.newDocumentBuilder();
            Document    document                            = builder.parse(coreFile);
            Element     coreDoc                             = document.getDocumentElement();
            NodeList    resources                           = coreDoc.getElementsByTagName("Resources");
            NodeList    typesList                           = ((Element) resources.item(0)).getElementsByTagName("Type");
            
            //Get All Override
            NodeList    overrideDoc                         = coreDoc.getElementsByTagName("Override");
            String      strOverrideFile                     = ((Element)((Element)overrideDoc.item(0)).getElementsByTagName("file").item(0)).getAttribute("path");
            File        file                                = new File(coreFile);
            String      path1                                = file.getParent();
            File        file2                               = new File(path1+strOverrideFile);
            
            
            //-----------------
            for (int i = 0; i < typesList.getLength(); i++) {
                Element resType = (Element) typesList.item(i);
                if (resType.getAttribute("id").equalsIgnoreCase(tabName)) {
                    NodeList list = ((Element) resType.getElementsByTagName("List").item(0)).getElementsByTagName("items");
                    NodeList items = ((Element) list.item(0)).getElementsByTagName("item");
                    resourcesList = new Resource[items.getLength()];
                    for (int j = 0; j < items.getLength(); j++) {
                        Element spriteItem = (Element) items.item(j);
                        Element binary = (Element) spriteItem.getElementsByTagName("binary").item(0);

                        String path = "..\\..\\..\\" + spriteItem.getAttribute("path");
                        String id = spriteItem.getAttribute("id");
                        String ind = binary.getAttribute("index");
                        if (!"".equals(ind)) {
                            int index = Integer.parseInt(ind);

                            resourcesList[index] = new Resource(path, id);
                        }
                    }
                    break;
                }
            }
            System.out.println("Parse successfull");
            System.out.println("Saving to file");

            // Generate and save list
            String spriteListString = "";
            for (int i = 0; i < resourcesList.length; i++) {
                if (resourcesList[i] != null) {
                    spriteListString += "\"" + resourcesList[i].id + "\" = \"" + resourcesList[i].path + "\"\r\n";
                }
            }

            if (listFile != null) {
                FileWriter writer = new FileWriter(listFile);
                writer = new FileWriter(listFile);
                writer.write(spriteListString);
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Error while generating sprite list for scenes");
            e.printStackTrace();
        }
    }
}
