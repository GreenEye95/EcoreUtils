# EcoreUtils: Ecore Model Utility Library

**EcoreUtils** is a Java-based utility library designed for working with Ecore models, GenModels, and related metadata transformations in the Eclipse Modeling Framework (EMF). It provides a set of powerful methods to simplify the management, validation, and conversion of Ecore and XSD (XML Schema Definition) models. This project is ideal for developers working with EMF-based models who need an easy-to-use, programmatic interface for common model transformations and verifications.

## Key Features

- **Ecore Model Verification:**  
  Easily check if a given file or path corresponds to a valid Ecore model with the `isEcoreModel()` method.

- **XSD to Ecore Conversion:**  
  Convert XML Schema (XSD) files into Ecore models with the `ConvertXSDToEcore()` method, allowing seamless integration of XSD-based schemas into the EMF ecosystem.

- **Ecore to GenModel Conversion:**  
  Generate a `GenModel` from an existing Ecore model. The `ConvertEcoreToGenModel()` method helps you create a GenModel file, which is the foundation for generating Java code for EMF models.

- **GenModel to Java Code Generation:**  
  Automatically generate Java source code from a GenModel file with the `GenModelToJava()` method. This is a critical step in automating code generation for model-driven development in EMF-based projects.

- **Model Validation:**  
  Validate the structure and conformance of models using the `validateModel()` method, ensuring that a model adheres to the specified metamodel.

- **Save Model After Verification:**  
  After validating a model, save it to a specified XMI file with the `saveModelAfterVerification()` method, ensuring persistence of validated models in a standardized format.

- **Print Ecore Model Structure:**  
  The `PrintEcoreStructure()` method provides a textual representation of the structure of an Ecore model, making it easier to understand its hierarchy and contents.

- **Ecore to XMI Conversion:**  
  Convert Ecore models to XMI (XML Metadata Interchange) format using the `ConvertEcoreToXMI()` method. XMI is a widely-used format for serializing EMF models.

- **Ecore to XSD Conversion:**  
  Export an Ecore model to an XSD file with the `ConvertEcoreToXSD()` method, facilitating the integration of EMF models into XML-based workflows.

- **Ecore to XSD Conversion:**  
  Export an svg file of uml representation with the `ConvertEcoreToUML()` method.

  
## Usage

This library provides a simple and clean API to interact with Ecore models, GenModels, and other related artifacts in the EMF ecosystem. Whether you're building a tool for model-driven development or need to convert between different model formats (Ecore, XSD, XMI), **EcoreUtils** offers a reliable solution.

### Example Usage

```java
EcoreUtils utils = new EcoreUtilsImpl();
String ecoreFilePath = "path/to/model.ecore";

// Check if the file is an Ecore model
boolean isEcore = utils.isEcoreModel(ecoreFilePath);

// Convert XSD to Ecore
boolean success = utils.ConvertXSDToEcore("path/to/schema.xsd", "path/to/output.ecore");

// Convert Ecore to GenModel
GenModel genModel = utils.ConvertEcoreToGenModel("path/to/model.ecore", "genModelDirectory", "MyGenModel.genmodel", "javaGenDirectory", "com.example");

// Generate Java code from GenModel
utils.GenModelToJava(genModel, "genModelDirectory");

// Validate model (Still not tested)
boolean isValid = utils.validateModel("modelString", "path/to/metamodel.ecore");

// Convert Ecore to XMI
boolean convertedToXMI = utils.ConvertEcoreToXMI("path/to/model.ecore", "path/to/output.xmi");

// Convert Ecore to UML
boolean convertedToUML = utils.ConvertEcoreToUML("path/to/model.ecore","path/to/output.svg")