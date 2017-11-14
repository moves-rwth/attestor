package de.rwth.i2.attestor.io;

import java.util.function.Consumer;

import org.json.JSONObject;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;

public class JsonImporter {

	public static HeapConfiguration parseHC( JSONObject hc, Consumer<String> addSelectorLabelFunction  ){
		if( Settings.getInstance().options().isIndexedMode() ){
			return JsonToIndexedHC.jsonToHC( hc, addSelectorLabelFunction );
		}else{
			return JsonToDefaultHC.jsonToHC( hc, addSelectorLabelFunction );
		}
	}
}
