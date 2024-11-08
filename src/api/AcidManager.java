package api;

import entity.AminoAcid;

import java.util.ArrayList;

public abstract class AcidManager {

    public abstract ArrayList<AminoAcid> createAcidSequence (String input);

}
