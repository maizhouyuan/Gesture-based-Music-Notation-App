package reaction;

import graphicslib.G;
import graphicslib.I;

public abstract class Mass extends Reaction.List implements I.Show{
    public Layer layer;
    public Mass(String layerName){    // Fetch out the actual layer name
        layer = Layer.byName.get(layerName);
        if(layer != null){
            layer.add(this);
        }else{
            System.out.println("Bad layer name: " + layerName);
        }
    }
    public void deleteMass(){
        clearAll();
        layer.remove(this);
    }

    // BUGFIX - ArrayList remove
    private int hashCode = G.rnd(100_000_000);
    public boolean equals(Object o){return this == o;}    // Referential test
    // Object in Java
    // primitive number/pointer/single char can fit in a slot
    // String can only be pointed rather than stay in a single slot, as an object, and with a more complicated structure
    // ==, referential equality, cheap easy one, pointing to the same object
    // equals, default defined in Java
    public int hashCode(){return hashCode;}
}
