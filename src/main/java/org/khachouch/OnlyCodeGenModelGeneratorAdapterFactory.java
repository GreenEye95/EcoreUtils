package org.khachouch;

import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.Monitor;

public class OnlyCodeGenModelGeneratorAdapterFactory extends GenModelGeneratorAdapterFactory {

    public static final GeneratorAdapterFactory.Descriptor Descriptor = new GeneratorAdapterFactory.Descriptor() {
        @Override
        public GeneratorAdapterFactory createAdapterFactory() {
            return new OnlyCodeGenModelGeneratorAdapterFactory();
        }
    };

    private final GenModelGeneratorAdapter onlyCodeGenModelGeneratorAdapter = new GenModelGeneratorAdapter(this) {
        @Override
        public void generateModelBuildProperties(GenModel genModel, Monitor monitor) {
            // Do nothing
        }

        @Override
        public void generateModelManifest(GenModel genModel, Monitor monitor) {
            // Do nothing
        }

        @Override
        public void generateModelModule(GenModel genModel, Monitor monitor) {
            // Do nothing
        }

        @Override
        public void generateModelPluginClass(GenModel genModel, Monitor monitor) {
            // Do nothing
        }

        @Override
        public void generateModelPluginProperties(GenModel genModel, Monitor monitor) {
            // Do nothing
        }
    };

    @Override
    public GenModelGeneratorAdapter createGenModelAdapter() {
        return onlyCodeGenModelGeneratorAdapter;
    }
}

