
import java.io.*;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

class Resource implements Comparator {

    protected String path;
    protected String id;
    protected int index;

    public Resource() {
    }

    public Resource(String path, String id, int index) {
        this.path = path;
        this.id = id;
        this.index = index;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (((Resource) o1).index > ((Resource) o2).index) {
            return (1);
        } else if (((Resource) o1).index < ((Resource) o2).index) {
            return (-1);
        } else {
            return (0);
        }
    }
}

public class SpriteListGen {

    public static void main(String[] args) {
        String cmd = "-core e:\\__Projects\\CowboysAndAliens\\W810i\\data\\triplet\\data.core -list test.list -tabname Sprites_In_Game";
        args = cmd.split(" ");
        try {
            ArrayList listCoreFiles = new ArrayList();
            String coreFile = null;
            String listFile = null;
            String tabName = null;            

            for (int i = 0; i < args.length; i++) {
                if ((args[i].compareToIgnoreCase("-core") == 0) && (i + 1 < args.length)) {
                    listCoreFiles.add(args[i + 1]);
                    System.out.println("-core " + args[i + 1]);
                } else if ((args[i].compareToIgnoreCase("-tabname") == 0) && (i + 1 < args.length)) {
                    tabName = args[i + 1];
                    System.out.println("-tabname " + args[i + 1]);
                } else if ((args[i].compareToIgnoreCase("-list") == 0) && (i + 1 < args.length)) {
                    listFile = args[i + 1];
                    System.out.println("-list " + args[i + 1]);
                }
            }
            //Resource[] resourcesList = null;
            Hashtable<String, Resource> resourcesList = new Hashtable<String, Resource>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document document;
            while (true) {
                int lastIndex = listCoreFiles.size() - 1;
                if (lastIndex < 0) {
                    break;
                }
                coreFile = (String) listCoreFiles.get(lastIndex);
                document = builder.parse(coreFile);
                Element coreDoc = document.getDocumentElement();
                NodeList overrideDoc = coreDoc.getElementsByTagName("Override");
                if (overrideDoc.getLength() == 0) {
                    break;
                }

                String overrideFile = ((Element) ((Element) overrideDoc.item(0)).getElementsByTagName("file").item(0)).getAttribute("path");
                File currentFile = new File(coreFile);
                String currentPath = currentFile.getParent();
                listCoreFiles.add(currentPath + "\\" + overrideFile);
            }

            for (int k = listCoreFiles.size() - 1; k >= 0; --k) {
                document = builder.parse((String) listCoreFiles.get(k));
                Element coreDoc = document.getDocumentElement();
                NodeList resources = coreDoc.getElementsByTagName("Resources");
                NodeList typesList = ((Element) resources.item(0)).getElementsByTagName("Type");
                for (int i = 0; i < typesList.getLength(); i++) {
                    Element resType = (Element) typesList.item(i);
                    if (resType.getAttribute("id").equalsIgnoreCase(tabName)) {
                        NodeList list = ((Element) resType.getElementsByTagName("List").item(0)).getElementsByTagName("items");
                        NodeList items = ((Element) list.item(0)).getElementsByTagName("item");
                        for (int j = 0; j < items.getLength(); j++) {
                            Element spriteItem = (Element) items.item(j);
                            Element binary = (Element) spriteItem.getElementsByTagName("binary").item(0);

                            String path = "..\\..\\..\\" + spriteItem.getAttribute("path");
                            String id = spriteItem.getAttribute("id");
                            int index = -1;
                            if (binary != null) {
                                String ind = binary.getAttribute("index");
                                if (!"".equals(ind)) {
                                    index = Integer.parseInt(ind);
                                }
                            }

                            String uid = spriteItem.getAttribute("uid");
                            String ignore = spriteItem.getAttribute("ignore");
                            Resource currentResource = resourcesList.get(uid);
                            if (currentResource != null) {
                                if (ignore.equalsIgnoreCase("1")) {
                                    resourcesList.remove(uid);
                                } else {
                                    if (!path.equalsIgnoreCase("..\\..\\..\\") && !currentResource.path.equalsIgnoreCase(path)) {
                                        currentResource.path = path;
                                    }
                                    if (index != -1 && currentResource.index != index) {
                                        currentResource.index = index;
                                    }
                                }
                            } else {
                                if (index != -1) {
                                    resourcesList.put(uid, new Resource(path, id, index));
                                }
                            }
                        }
                        break;
                    }
                }
            }

            System.out.println("Parse successfull");
            System.out.println("Saving to file");

            // Generate and save list
            String spriteListString = "";
            Resource rc = new Resource();
            Object[] resource = resourcesList.values().toArray();            
            Arrays.sort(resource, (Comparator) rc);
            for (int i = 0; i < resource.length; ++i) {
                Resource r = (Resource) resource[i];
                spriteListString += "\"" + r.index + "\" = \"" + r.path + "\"\r\n";
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
