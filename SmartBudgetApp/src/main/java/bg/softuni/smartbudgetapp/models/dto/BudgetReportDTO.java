package bg.softuni.smartbudgetapp.models.dto;


/**
 * DTO клас за данните, използвани в отчета за бюджета.
 * Съдържа информация за категория и сума за графично представяне.
 */
public class BudgetReportDTO {

    private String category;
    private double amount;
    private String color;

    public BudgetReportDTO() {
    }

    public BudgetReportDTO(String category, double amount) {
        this.category = category;
        this.amount = amount;
        // Генерираме случаен цвят за графиката
        this.color = generateRandomColor();
    }

    /**
     * Генерира случаен цвят в HEX формат за използване в графиките.
     *
     * @return String HEX цвят (например "#FF5733")
     */
    private String generateRandomColor() {
        // Предварително дефинирани цветове за различни категории
        String[] predefinedColors = {
                "#FF6384", // червено-розов
                "#36A2EB", // син
                "#FFCE56", // жълт
                "#4BC0C0", // тюркоазен
                "#9966FF", // лилав
                "#FF9F40", // оранжев
                "#C9CBCF", // сив
                "#7CFC00", // зелен
                "#FF7F50", // корал
                "#8A2BE2"  // синьо-виолетов
        };

        // Използваме хеш кода на името на категорията, за да изберем цвят
        int index = Math.abs(category.hashCode() % predefinedColors.length);
        return predefinedColors[index];
    }

    // Getters and Setters

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }





}
