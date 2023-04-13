package model;
import java.util.Arrays;

public enum ShapeType {
    CIRCLE("Circle", false),
    SQUARE("Square", false),
    DIAMOND("Diamond", false),
    SPIRAL("Spiral", false),
    LOOSE_SPIRAL("Loose Spiral", false),
    SUNFLOWER("Sunflower", false);

    private final String name;
    private boolean isSelected;

    private ShapeType(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public static void resetAllStates() {
        Arrays.stream(ShapeType.values())
                .forEach(shapeType -> shapeType.setIsSelected(false));
    }

    public void flipIsSelectedState() {
        this.isSelected = !this.isSelected;
    }

    public static ShapeType fromName(String name) {
        return Arrays.stream(ShapeType.values())
                .filter(shapeType -> shapeType.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static boolean AtLeastOneShapeIsActivated() {
        return Arrays.stream(ShapeType.values()).anyMatch(ShapeType::isSelected);
    }
}
