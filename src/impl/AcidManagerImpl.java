package impl;

import api.AcidManager;
import entity.AminoAcid;
import types.AcidType;

import java.util.ArrayList;

public class AcidManagerImpl extends AcidManager {
    private ArrayList<AminoAcid> acids;
    @Override
    public ArrayList<AminoAcid> createAcidSequence(String input) {
        acids = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            AminoAcid acid = new AminoAcid(i);
            if (i == 0)
                acid.setCoordinates(0,0);
            acids.add(acid);
        }
        return acids;
    }
}
