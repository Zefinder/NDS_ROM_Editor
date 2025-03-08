package pokemon.files.types;

public abstract class TypedVariable {

	private TypeEnum type;

	public TypedVariable(TypeEnum type) {
		this.type = type;
	}

	public TypeEnum getType() {
		return type;
	}
	
	public abstract int getSize();

}
