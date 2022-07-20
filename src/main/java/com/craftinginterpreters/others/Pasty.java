package com.craftinginterpreters.others;

/**
 * @author fan
 * 7/20/22
 */
abstract class Pasty {
    abstract void accept(PastryVisitor visitor);
}
class Beignet extends Pasty{

    @Override
    void accept(PastryVisitor visitor) {
        visitor.visitBeignet(this);

    }
}

class Cruller extends Pasty {
    @Override
    void accept(PastryVisitor visitor) {
        visitor.visitCruller(this);

    }
}

interface PastryVisitor {
    void visitBeignet(Beignet beignet);
    void visitCruller(Cruller cruller);


}