package de.rwth.i2.attestor.io.jsonImport;

import java.util.function.Consumer;

import de.rwth.i2.attestor.main.environment.SceneObject;
import org.json.JSONObject;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

public class JsonImporter extends SceneObject {

	public JsonImporter(SceneObject sceneObject) {
		super(sceneObject);
	}

	public HeapConfiguration parseHC( JSONObject hc, Consumer<String> addSelectorLabelFunction  ){
		if( scene().options().isIndexedMode() ){
			JsonToIndexedHC importer = new JsonToIndexedHC(this);
			return importer.jsonToHC(hc, addSelectorLabelFunction);
		}else{
			JsonToDefaultHC importer = new JsonToDefaultHC(this);
			return importer.jsonToHC(hc, addSelectorLabelFunction);
		}
	}
}
