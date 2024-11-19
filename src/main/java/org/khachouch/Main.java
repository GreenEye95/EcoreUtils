package org.khachouch;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // This line sets a system property to add an export directive for the java.desktop module.
        // Specifically, it allows the unnamed module (our application) to access the internal package
        // com.sun.imageio.plugins.png. This is necessary because the PlantUML library we are using
        // attempts to access the PNGMetadata class within this package. Without this directive,
        // the JVM will throw an IllegalAccessError, as the java.desktop module does not export
        // this package by default. Adding this export directive ensures that our application
        // can access the necessary internal classes and avoid runtime errors.
        System.setProperty("jdk.module.addexports", "java.desktop/com.sun.imageio.plugins.png=ALL-UNNAMED");


        System.out.println("Hello, World!");
        String ecoreFilePath = "src/main/resources/GastmEcore/gastm.ecore";
        String xsdDestinationPath = "src/main/resources/GastmEcore/gastm.xsd";
        String xmiDestinationPath = "src/main/resources/GastmEcore/gastm.xmi";
        String xsdXMIPath = "src/main/resources/GastmEcore/XMI.xsd";
        String ecoreXMIFilePath = "src/main/resources/GastmEcore/XMI.ecore";
        String ecoreCSTFilePath = "src/main/resources/GastmEcore/CST.ecore";

        EcoreUtils ecoreUtils = new EcoreUtilsImpl();
        // XSD to Ecore

        var successXsdToEcore = ecoreUtils.ConvertXSDToEcore(xsdXMIPath, ecoreXMIFilePath);
        if (successXsdToEcore) {
            System.out.println("Xsd converted to Ecore : Success");
        } else {
            System.out.println("Xsd converted to Ecore : failure");
        }

        // Ecore to Java
        String genModelFileName = new File(ecoreFilePath).getName().replaceFirst("[.][^.]+$", "");
        GenModel genModel = ecoreUtils.ConvertEcoreToGenModel(ecoreFilePath,
                                                     new File(ecoreFilePath).getParent().replace("\\","/"),
                                                                 genModelFileName,
                                                  "src/main/java/",
                                                     "org.khachouch.ModelGenJava");
        //String genModelPath = new File(ecoreFilePath).getParent()+ separator+genModelFileName;
        var successEcoreToGenModel = ecoreUtils.GenModelToJava(genModel,
                                                       "src/main/java/");
        if (successEcoreToGenModel) {
            System.out.println("Ecore to Java : Success");
        } else {
            System.out.println("Ecore to Java  : failure");
        }

        // check if ecore model
        var successCheckEcoreModel = ecoreUtils.isEcoreModel(ecoreFilePath);
        if (successCheckEcoreModel) {
            System.out.println("Ecore check Model : Success");
        } else {
            System.out.println("Ecore check Model : failure");
        }
        var successCheckEcoreModel2 = ecoreUtils.isEcoreModel(xsdXMIPath);
        if (!successCheckEcoreModel2) {
            System.out.println("Ecore check Model : Success (Not Ecore)");
        } else {
            System.out.println("Ecore check Model : failure");
        }

        // print ecore structure
        ecoreUtils.PrintEcoreStructure(ecoreFilePath);

        // Ecore to XMI
        var successEcoreToXmi = ecoreUtils.ConvertEcoreToXMI(ecoreFilePath, xmiDestinationPath);
        if (successEcoreToXmi) {
            System.out.println("Ecore converted to XMI : Success");
        } else {
            System.out.println("Ecore converted to XMI : failure");
        }

        // Ecore to XSD
        var succesEcoreToXsd = ecoreUtils.ConvertEcoreToXSD(ecoreFilePath, xsdDestinationPath);
        if (succesEcoreToXsd) {
            System.out.println("Ecore converted to XSD : Success");
        } else {
            System.out.println("Ecore converted to XSD : failure");
        }

        // Ecore to UML


        var successEcoreToUML = ecoreUtils.ConvertEcoreToUML(ecoreFilePath,
                //ecoreFilePath.replace(".ecore",".xmi"),
                ecoreFilePath.replace(".ecore",".svg"));
        if (successEcoreToUML) {
            System.out.println("Ecore converted to UML : Success");
        } else {
            System.out.println("Ecore converted to UML : failure");
        }
        var successEcoreToUML2 = ecoreUtils.ConvertEcoreToUML(ecoreXMIFilePath,
                //ecoreFilePath.replace(".ecore",".xmi"),
                ecoreXMIFilePath.replace(".ecore",".svg"));
        if (successEcoreToUML2) {
            System.out.println("Ecore converted to UML : Success");
        } else {
            System.out.println("Ecore converted to UML : failure");
        }
        var successEcoreToUML3 = ecoreUtils.ConvertEcoreToUML(ecoreCSTFilePath,
                //ecoreFilePath.replace(".ecore",".xmi"),
                ecoreCSTFilePath.replace(".ecore",".svg"));
        if (successEcoreToUML3) {
            System.out.println("Ecore converted to UML : Success");
        } else {
            System.out.println("Ecore converted to UML : failure");
        }
    }
}