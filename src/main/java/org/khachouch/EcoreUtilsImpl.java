package org.khachouch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.*;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.merge.java.JControlModel;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xsd.*;
import org.eclipse.xsd.ecore.EcoreSchemaBuilder;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;

import java.io.*;
import java.util.*;

public class EcoreUtilsImpl implements EcoreUtils {
    private final ExtensibleURIConverterImpl converter;
    private static Logger logger;
    private Monitor monitor;

    public EcoreUtilsImpl() {

        logger = (Logger) LogManager.getLogger(EcoreUtilsImpl.class);

        converter = new ExtensibleURIConverterImpl();
        // Enregistrer la fabrique de ressource XMI pour le format Ecore
        monitor = new Monitor(logger);
    }

    /**
     * Prints the validation issues found in the Ecore model.
     *
     * @param diagnostic The Diagnostic object containing the validation issues.
     */
    private static void printValidationIssues(Diagnostic diagnostic) {
        for (Diagnostic issue : diagnostic.getChildren()) {
            System.out.println("Severity: " + issue.getSeverity());
            System.out.println("Message: " + issue.getMessage());
        }
    }

    /**
     * Validates an Ecore file and prints the validation results.
     *
     * @param ecoreFilePath The path to the Ecore file to be validated.
     */
    public boolean isEcoreModel(String ecoreFilePath) {
        try {
            // Load the Ecore file as an EMF Resource
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new EcoreResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());

            Resource ecoreResource = resourceSet.getResource(URI.createFileURI(ecoreFilePath), true);

            // Validate the Ecore model
            Diagnostic diagnostic = Diagnostician.INSTANCE.validate(ecoreResource.getContents().get(0));

            // Check the validation result
            if (diagnostic.getSeverity() == Diagnostic.OK) {
                System.out.println("The Ecore file is valid.");
                return true;
            } else {
                System.out.println("The Ecore file is invalid.");
                printValidationIssues(diagnostic);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error loading or validating the Ecore file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts an XSD file to an Ecore model and saves it to a specified location.
     *
     * @param xsdUri   The URI of the XSD file to be converted.
     * @param ecoreUri The URI where the generated Ecore model will be saved.
     * @return
     */
    public boolean ConvertXSDToEcore(String xsdUri, String ecoreUri) {
        // Create an XSDEcoreBuilder object to handle the conversion from XSD to Ecore
        XSDEcoreBuilder xsdEcoreBuilder = new XSDEcoreBuilder();

        // Create a URI object from the XSD file path
        // This URI will be used to locate the XSD file
        URI xsdURI = URI.createURI(xsdUri);

        // Generate a collection of Ecore packages from the XSD file
        // The XSDEcoreBuilder processes the XSD and produces corresponding Ecore packages
        Collection ecorePackages = xsdEcoreBuilder.generate(xsdURI);

        // Create a URI object from the Ecore file path
        // This URI will be used to specify where the Ecore model will be saved
        URI ecoreURI = URI.createURI(ecoreUri);

        // Create an Ecore resource from the URI
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        Resource ecoreResource = resourceSet.createResource(ecoreURI);

        // Add the generated Ecore packages to the resource
        // This step integrates the Ecore packages into the resource for saving
        ecoreResource.getContents().addAll(ecorePackages);

        // Save the Ecore resource to the specified location
        try {
            ecoreResource.save(null);
            return true;
        } catch (IOException e) {
            // Print an error message if saving the Ecore model fails
            System.err.println("ERROR saving the Ecore model: " + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts an Ecore model to a GenModel and saves it to a specified directory.
     *
     * @param ecorePath        The path to the Ecore file.
     * @param genDirectory     The directory where the GenModel will be saved.
     * @param genModelFileName The name of the GenModel file (without extension).
     * @param javaGenDirectory The directory used for Java code generation.
     * @param basePackage      The base package name for the generated classes.
     * @return The generated GenModel object.
     */
    public GenModel ConvertEcoreToGenModel(String ecorePath, String genDirectory, String genModelFileName, String javaGenDirectory, String basePackage) {
        // Create a ResourceSet, which manages a set of related resources (models).
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register factories for different file extensions to handle model files.
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new EcoreResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());

        // Map "platform:/resource/" URIs to a local file path for resolving references.
        resourceSet.getURIConverter().getURIMap().put(
                URI.createURI("platform:/resource/"),
                URI.createFileURI(new File(javaGenDirectory) + File.separator)
        );

        // Register the GenModel package in the package registry of the ResourceSet.
        resourceSet.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Load the Ecore model from the specified path.
        URI ecoreURI = URI.createURI(ecorePath, true); // Create a URI for the Ecore file.
        Resource resource = resourceSet.getResource(ecoreURI, true); // Load the Ecore resource.
        EPackage ePackage = (EPackage) resource.getContents().get(0); // Get the root EPackage.

        // Create a new GenModel instance.
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.getForeignModel().add(ecorePath); // Reference the Ecore file in the GenModel.
        genModel.initialize(Collections.singleton(ePackage)); // Initialize the GenModel with the EPackage.

        // Set basic properties for the GenModel.
        genModel.setModelDirectory(genDirectory); // Directory where generated models will be saved.
        String modelName = ePackage.getNsPrefix(); // Use the EPackage namespace prefix as the model name.
        genModel.setModelName(modelName);
        genModel.setComplianceLevel(GenJDKLevel.JDK210_LITERAL); // Set Java compliance level to JDK 21.
        genModel.setUpdateClasspath(false); // Do not update classpath automatically.
        genModel.setGenerateSchema(true); // Enable schema generation.
        genModel.setCanGenerate(true); // Allow the GenModel to be generated.

        // Iterate through the GenPackages to set proper base package names and prefixes.
        List<GenPackage> genPackages = genModel.getGenPackages().stream().toList();
        for (GenPackage genPackage : genPackages) {
            genPackage.setBasePackage(basePackage); // Set the base package for each GenPackage.

            // Adjust the prefix for proper class naming.
            genPackage.setPrefix(
                    genPackage.getPrefix().lastIndexOf('.') != -1
                            ? (genPackage.getPrefix().lastIndexOf('.', genPackage.getPrefix().lastIndexOf('.') - 1) != -1
                            ? genPackage.getPrefix().substring(
                            genPackage.getPrefix().lastIndexOf('.', genPackage.getPrefix().lastIndexOf('.') - 1) + 1,
                            genPackage.getPrefix().lastIndexOf('.')
                    )
                            : (genPackage.getPrefix().lastIndexOf('.') != 0
                            ? genPackage.getPrefix().substring(genPackage.getPrefix().lastIndexOf('.') + 1)
                            : genPackage.getPrefix())
                    )
                            : genPackage.getPrefix()
            );
        }

        // Also update the base package of the first GenPackage explicitly.
        GenPackage genPackage = genModel.getGenPackages().get(0);
        genPackage.setBasePackage(basePackage);

        // Save the GenModel to a file in the specified directory.
        String genModelFilePath = genDirectory + "/" + genModelFileName + ".genmodel"; // Full path for the GenModel file.
        URI genModelURI = URI.createURI(genModelFilePath, true); // Create a URI for the GenModel file.
        Resource genModelResource = resourceSet.createResource(genModelURI); // Create a resource for the GenModel.
        if (genModelResource == null) {
            System.err.println("\tERROR: Could not create a resource for the URI: " + genModelFilePath);
            return null; // Exit if the resource could not be created.
        }

        // Add the GenModel object to the resource contents.
        genModelResource.getContents().add(genModel);

        // Save the resource to persist the GenModel file.
        try {
            genModelResource.save(Collections.EMPTY_MAP); // Save with an empty options map.
        } catch (IOException e) {
            e.printStackTrace(); // Print the exception stack trace if saving fails.
        }

        // Return the generated GenModel object.
        return genModel;
    }


    /**
     * Validates a model against a given metamodel.
     *
     * @param modelString   The string representation of the model to be validated.
     * @param metaModelPath The path to the metamodel file.
     * @return true if the model is valid, false otherwise.
     */
    public boolean validateModel(String modelString, String metaModelPath) {
        // Create a File object for the metamodel file
        File file = new File(metaModelPath);
        // Load the metamodel resource from the file
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        Resource res = resourceSet.getResource(URI.createFileURI(file.getAbsolutePath()), true);
        // Get the EPackage (metamodel) from the resource's contents
        var metaModel = (EPackage) res.getContents().get(0);
        // Register the metamodel in the package registry
        resourceSet.getPackageRegistry().put(metaModel.getNsURI(), metaModel);

        // Create a new resource for the model to be validated
        Resource resource1 = resourceSet.createResource(URI.createURI("src/main/resources/temp.xmi"));
        try {
            // Load the model from the string representation
            resource1.load(new java.io.ByteArrayInputStream(modelString.getBytes()), null);
            // Get the root object of the model
            EObject modelRoot = resource1.getContents().get(0);

            // Create a Diagnostician to validate the model
            Diagnostician diagnostician = new Diagnostician();
            // Validate the model root object
            Diagnostic diagnostic = diagnostician.validate(modelRoot);
            // Check if the validation result indicates an error
            boolean isValid = diagnostic.getSeverity() != Diagnostic.ERROR;
            // If the model is not valid, print the diagnostic messages
            if (!isValid) {
                for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
                    System.out.println(childDiagnostic.getMessage());
                }
            }
            // Return the validation result
            return isValid;
        } catch (Exception e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
            return false;
        } finally {
            // Unload the resource to free up memory
            resource1.unload();
        }
    }

    /**
     * Validates a model and saves it as an XMI file if valid.
     *
     * @param modelString   The string representation of the model to be validated and saved.
     * @param metaModelPath The path to the metamodel file.
     * @param xmiFilePath   The path where the XMI file will be saved.
     * @return true if the model is valid and saved successfully, false otherwise.
     */
    public boolean saveModelAfterVerification(String modelString, String metaModelPath, String xmiFilePath) {
        // Validate the model against the metamodel
        boolean isValid = validateModel(modelString, metaModelPath);
        // Print the validation result
        System.out.println("Le modèle est valide : " + isValid);

        // If the model is valid, save it as an XMI file
        if (isValid) {
            // Create a new resource for the XMI file
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
            Resource resource = resourceSet.createResource(URI.createURI(xmiFilePath));
            try {
                // Load the model from the string representation into the resource
                resource.load(new ByteArrayInputStream(modelString.getBytes()), null);
                // Save the resource to the specified XMI file path
                resource.save(null);
                // Print a success message
                System.out.println("Fichier XMI sauvegardé avec succès à l'emplacement : " + xmiFilePath);
                return true;
            } catch (IOException e) {
                // Print the stack trace if an exception occurs during saving
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Prints the structure of an Ecore model to the console, including packages, classifiers, and structural features.
     *
     * @param ecoreFilePath The absolute or relative path to the input Ecore file.
     */
    public void PrintEcoreStructure(String ecoreFilePath) {
        try {
            // Create a new ResourceSet to manage a set of related EMF resources.
            ResourceSet resourceSet = new ResourceSetImpl();

            // Register a resource factory to handle .ecore files.
            // This ensures that the Ecore file can be loaded and interpreted.
            resourceSet.getResourceFactoryRegistry()
                    .getExtensionToFactoryMap()
                    .put("ecore", new EcoreResourceFactoryImpl());

            // Construct a URI object for the Ecore file.
            // Convert the file path to an absolute path to ensure it is properly resolved.
            URI ecoreURI = URI.createFileURI(new File(ecoreFilePath).getAbsolutePath());

            // Load the Ecore resource from the specified URI.
            Resource ecoreResource = resourceSet.getResource(ecoreURI, true);

            // Load the resource's contents. EMPTY_MAP indicates no additional load options.
            ecoreResource.load(Collections.EMPTY_MAP);

            // Print the top-level contents of the loaded Ecore resource for debugging.
            System.out.println("Contents of Ecore resource: " + ecoreResource.getContents());

            // Iterate through all the top-level objects in the resource's contents.
            for (EObject eObject : ecoreResource.getContents()) {
                // Check if the object is an EPackage (the main container in Ecore models).
                if (eObject instanceof EPackage) {
                    EPackage ePackage = (EPackage) eObject;

                    // Print the name of the EPackage.
                    System.out.println("EPackage: " + ePackage.getName());

                    // Iterate through the classifiers (e.g., classes, data types) in the EPackage.
                    for (EClassifier eClassifier : ePackage.getEClassifiers()) {
                        // Print the name of the EClassifier.
                        System.out.println("  EClassifier: " + eClassifier.getName());

                        // If the classifier is an EClass, print its structural features (attributes and references).
                        if (eClassifier instanceof EClass) {
                            EClass eClass = (EClass) eClassifier;

                            // Iterate through the structural features of the EClass.
                            for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
                                // Print the name of each structural feature.
                                System.out.println("    EStructuralFeature: " + feature.getName());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Handle exceptions related to file input/output.
            System.err.println("File I/O error: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions that may occur during execution.
            System.err.println("Error: " + e.getMessage());
        }
    }



    /**
     * Converts an Ecore file to an XMI file.
     *
     * @param ecorePath The path to the input Ecore file (.ecore).
     * @param xmiDestinationPath The path to the output XMI file (.xmi).
     * @return true if the conversion was successful, false otherwise.
     */
    public boolean ConvertEcoreToXMI(String ecorePath, String xmiDestinationPath) {
        try {
            // Initialize a resource set to manage resources.
            ResourceSet resourceSet = new ResourceSetImpl();

            // Register the XMI and Ecore resource factories.
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

            // Load the Ecore model as a resource using its URI.
            Resource ecoreResource = resourceSet.getResource(URI.createFileURI(ecorePath), true);

            // Load the contents of the Ecore model into memory.
            ecoreResource.load(Collections.EMPTY_MAP);

            // Initialize a new XMI resource for saving the model in XMI format.
            Resource xmiResource = resourceSet.createResource(URI.createFileURI(xmiDestinationPath));

            // Check if the xmiResource was successfully created.
            if (xmiResource == null) {
                System.err.println("Failed to create XMI resource for the destination path: " + xmiDestinationPath);
                return false;
            }

            // Transfer all root elements from the Ecore resource to the XMI resource.
            xmiResource.getContents().addAll(ecoreResource.getContents());

            // Save the XMI resource to the specified path.
            xmiResource.save(Collections.EMPTY_MAP);
            return true;
        } catch (IOException e) {
            System.err.println("IOException occurred during conversion: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            System.err.println("Runtime exception occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts an Ecore model to an XSD schema and saves it to the specified path.
     *
     * @param ecoreFilePath Path to the input Ecore file.
     * @param xsdOutputPath Path to save the generated XSD file.
     * @return true if the XSD file is generated successfully, false otherwise.
     */
    public boolean ConvertEcoreToXSD(String ecoreFilePath, String xsdOutputPath) {
        try {
            // Step 1: Initialize the ResourceSet and register necessary packages and resource factories.
            // The ResourceSet is used to manage resources such as Ecore and XSD files.
            ResourceSet resourceSet = new ResourceSetImpl();

            // Register the GenModel package for schema building.
            resourceSet.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

            // Register resource factories to handle different file types.
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new EcoreResourceFactoryImpl());

            // Step 2: Load the Ecore resource using its file path.
            // Create a URI for the Ecore file.
            URI ecoreURI = URI.createFileURI(ecoreFilePath);

            // Load the resource into the ResourceSet.
            Resource ecoreResource = resourceSet.getResource(ecoreURI, true);

            // Check if the Ecore resource contains at least one valid EPackage.
            if (ecoreResource.getContents().isEmpty() || !(ecoreResource.getContents().get(0) instanceof EPackage)) {
                throw new IllegalArgumentException("The provided Ecore file does not contain a valid EPackage.");
            }

            // Retrieve the root EPackage from the loaded resource.
            EPackage ePackage = (EPackage) ecoreResource.getContents().get(0);

            // Step 3: Generate a GenModel for the Ecore model.
            // The GenModel is required by the EcoreSchemaBuilder for XSD generation.
            String genDirectory = ecoreFilePath.substring(0, ecoreFilePath.lastIndexOf("/")); // Directory of the Ecore file.
            String genModelFileName = new File(ecoreFilePath).getName().replaceFirst("[.][^.]+$", ""); // File name without extension.
            String basePackage = ""; // Base package for the generated model.

            // Use a helper method to create the GenModel.
            GenModel genModel = this.ConvertEcoreToGenModel(ecoreFilePath, genDirectory, genModelFileName, "", basePackage);

            // Step 4: Initialize the EcoreSchemaBuilder using the ExtendedMetaData from the GenModel.
            // ExtendedMetaData contains information needed to map the Ecore model to XSD.
            EcoreSchemaBuilder schemaBuilder = new EcoreSchemaBuilder(genModel.getExtendedMetaData());

            // Step 5: Generate the XSD schema from the EPackage.
            XSDSchema xsdSchema = schemaBuilder.getSchema(ePackage);

            // Step 6: Save the generated XSD schema to the specified output path.
            // Create a URI for the output XSD file.
            URI xsdURI = URI.createFileURI(xsdOutputPath);

            // Create a new resource for the XSD file in the ResourceSet.
            Resource xsdResource = resourceSet.createResource(xsdURI);

            // Add the XSD schema to the resource's contents.
            xsdResource.getContents().add(xsdSchema);

            // Save the resource to write the XSD schema to the file.
            xsdResource.save(Collections.EMPTY_MAP);

            // Return true to indicate successful processing.
            return true;

        } catch (IOException | IllegalArgumentException e) {
            // Catch and handle file I/O errors or invalid arguments.
            e.printStackTrace();
            // Return false to indicate failure.
            return false;
        }
    }


    /**
     * Main method to generate Java code from a GenModel file.
     * This method initializes a code generator and produces Java code based on the provided GenModel.
     * The generated code will be placed in the target directory.
     *
     * @param genModel The GenModel object representing the model to be converted into Java code.
     * @param targetPath The target directory where the generated Java code should be stored.
     * @return boolean indicating whether the generation was successful (always returns true in this case).
     */
    public boolean GenModelToJava(GenModel genModel, String targetPath) {

        // Save the current error output stream to restore it later (in case we change it temporarily).
        PrintStream oldErr = System.err;

        try {
            // Temporarily redirect System.err to suppress any error messages that might be printed
            // during the generation process (to avoid cluttering the console).
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
        } catch (Throwable e) {
            // In case of any error while redirecting System.err (e.g., security exceptions),
            // set 'oldErr' to null to avoid further restoration attempts.
            oldErr = null;
        }

        try {
            // Set the model directory to the root. This is a default setting for code generation.
            // It can be adjusted if required based on the actual directory structure.
            genModel.setModelDirectory("/");

            // Enable the code generation process. This is a flag that tells the generator
            // whether it is allowed to perform code generation.
            genModel.setCanGenerate(true);

            // Create a custom generator object that will be responsible for the actual code generation.
            Generator generator;
            generator = new Generator() {
                @Override
                public JControlModel getJControlModel() {
                    // Return a new JControlModel object (could be used for controlling code generation specifics).
                    return new JControlModel();
                }
            };

            // Register a custom descriptor for the GenModel's URI to the generator's adapter factory.
            // This is necessary to tell the generator which adapter to use for this specific model type.
            generator.getAdapterFactoryDescriptorRegistry().addDescriptor(GenModelPackage.eNS_URI,
                    OnlyCodeGenModelGeneratorAdapterFactory.Descriptor);

            // Set the input GenModel for the generator to use. This is the model that will be processed
            // to generate Java code.
            generator.setInput(genModel);

            // Start the generation process. We specify that the type of project to generate is a "MODEL_PROJECT_TYPE"
            // and pass the monitor (a progress monitor object to track the generation process).
            generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, this.monitor);

        } finally {
            // Ensure that we restore the original error output stream after the generation process is complete.
            // This is done regardless of whether the generation succeeded or failed.
            if (oldErr != null) {
                System.setErr(oldErr);
            }
        }

        // Return true to indicate that the method has completed successfully.
        // Note: In this case, success is assumed, regardless of whether the generation actually worked.
        return true;
    }
}
