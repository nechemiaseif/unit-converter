package nseif.unitconverter.models;

public class UnitConverter {
    public enum InputType {
        KM, MILES
    }

    private double input, output;
    private InputType inputType;

    public UnitConverter() {
        this(0);
    }

    public UnitConverter(double input) {
        this(InputType.MILES, input);
    }

    public UnitConverter(InputType inputType, double input) {
        this.inputType = inputType;
        this.input = input;
    }

    public double getInput()
    {
        return input;
    }

    public void setInput(double input)
    {
        this.input = input;
    }

    public double getOutput()
    {
        return output;
    }

    public void setOutput(double output)
    {
        this.output = output;
    }

    public InputType getInputType()
    {
        return inputType;
    }

    public void setInputTypeFromOrdinal(int inputTypeOrdinal)
    {
        this.inputType = InputType.values()[inputTypeOrdinal];
    }

    public void convert(InputType inputType, double input)
    {
        setInput(input);

        switch(inputType)
        {
            case KM:
                setOutput(convertKmToMiles(input));
                break;
            case MILES:
                setOutput(convertMilesToKm(input));
                break;
        }
    }


    private double convertMilesToKm(double miles){
        return miles * 1.60934;
    }

    private  double convertKmToMiles(double km){
        return km * 0.621371;
    }
}

