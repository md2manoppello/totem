/**
 * It adds a new non-mandatory metaclass (i.e., a new class which will be the type of a reference with lower bound 0) 
 */
package mmfootprint.evaluation.mutators.nonbreaking;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;

import mmfootprint.evaluation.mutators.AbstractMutator;
import mmfootprint.utils.MMResource;

public class AddOptionalMetaclass extends AbstractMutator {

	@Override
	public void mutate(MMResource metamodel, String outputFolder) {
		// obtain candidate source classes (all)
		List<EClass> candidates = new ArrayList<EClass>();
		candidates.addAll(metamodel.getEClasses());
		
		// generate each possible mutant
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		for (EClass clazz : candidates) {
			// add new class to the meta-model 
			EPackage pack = (EPackage)clazz.eContainer(); 
			EClass   newClass = factory.createEClass();
			newClass.setName( this.getRandomString(6) );
			pack.getEClassifiers().add(newClass);
			
			// add new optional reference to the candidate class
			EReference   newRef   = factory.createEReference();
			newRef.setEType(newClass);
			newRef.setLowerBound(0);
			newRef.setName( this.getRandomString(6) );
			clazz.getEStructuralFeatures().add(newRef);
			
			// register mutation
			EAnnotation annotation = registerMutation(new ENamedElement[]{newClass, newRef}, null, null);
			
			// save mutant			
			save(metamodel, outputFolder);
			
			// unregister mutation
			unregisterMutation(annotation);
			
			// undo the mutation
			clazz.getEStructuralFeatures().remove(newRef);
			pack.getEClassifiers().remove(newClass);
		}
	}

	@Override
	public boolean isBreaking() {
		return false;
	}
}
