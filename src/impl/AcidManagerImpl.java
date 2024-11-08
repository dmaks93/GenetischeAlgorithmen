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
            AcidType type = AcidType.UNKNOWN;
            if(input.charAt(i) == '0')
                type = AcidType.BLACK;
            else if (input.charAt(i) == '1') {
                type = AcidType.WHITE;
            }
            AminoAcid acid = new AminoAcid(type, i);
            if (i == 0)
                acid.setCoordinates(0,0);
            acids.add(acid);
        }
        return acids;
    }
}
