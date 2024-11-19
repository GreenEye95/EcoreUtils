package org.khachouch;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;

public interface EcoreUtils {
    public boolean isEcoreModel(String path);
    public boolean ConvertXSDToEcore(String xsdUri, String ecoreUri);
    public GenModel ConvertEcoreToGenModel(String ecorePath, String genDirectory, String genModelFileName, String javaGenDirectory, String basePackage);
    public boolean GenModelToJava(GenModel genModel, String genPath);
    public boolean validateModel(String modelString, String metaModelPath);
    public boolean saveModelAfterVerification(String modelString, String metaModelPath, String xmiFilePath);
    public void PrintEcoreStructure(String ecoreFilePath);
    public boolean ConvertEcoreToXMI(String ecorePath, String xmiDestinationPath);
    public boolean ConvertEcoreToXSD(String ecoreFilePath, String xsdFilePath);
    public boolean ConvertEcoreToUML(String ecoreFilePath, String svgFilePath);
}
