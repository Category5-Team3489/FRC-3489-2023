package frc.robot.enums;

public enum LedPattern {
    RainbowRainbowPalette(-0.99),
    RainbowPartyPalette(-0.97),
    RainbowOceanPalette(-0.95),
    RainbowLavePalette(-0.93),
    RainbowForestPalette(-0.91),
    RainbowWithGlitter(-0.89),
    Confetti(-0.87),
    ShotRed(-0.85),
    ShotBlue(-0.83),
    ShotWhite(-0.81),
    SinelonRainbowPalette(-0.79),
    SinelonPartyPalette(-0.77),
    SinelonOceanPalette(-0.75),
    SinelonLavaPalette(-0.73),
    SinelonForestPalette(-0.71),
    BeatsPerMinuteRainbowPalette(-0.69),
    BeatsPerMinutePartyPalette(-0.67),
    BeatsPerMinuteOceanPalette(-0.65),
    BeatsPerMinuteLavaPalette(-0.63),
    BeatsPerMinuteForestPalette(-0.61),
    FireMedium(-0.59),
    FireLarge(-0.57),
    TwinklesRainbowPalette(-0.55),
    TwinklesPartyPalette(-0.53),
    TwinklesOceanPalette(-0.51),
    TwinklesLavaPalette(-0.49),
    TwinklesForestPalette(-0.47),
    ColorWavesRainbowPalette(-0.45),
    ColorWavesPartyPalette(-0.43),
    ColorWavesOceanPalette(-0.41),
    ColorWavesLavaPalette(-0.39),
    ColorWavesForestPalette(-0.37),
    LarsonScannerRed(-0.35),
    LarsonScannerGray(-0.33),
    LightChaseRed(-0.31),
    LightChaseBlue(-0.29),
    LightChaseGray(-0.27),
    HeartbeatRed(-0.25),
    HeartbeatBlue(-0.23),
    HeartbeatWhite(-0.21),
    HeartbeatGray(-0.19),
    BreathRed(-0.17),
    BreathBlue(-0.15),
    BreathGray(-0.13),
    StrobeRed(-0.11),
    StrobeBlue(-0.09),
    StrobeGold(-0.07),
    StrobeWhite(-0.05),
    EndToEndBlendToBlackColor1(-0.03),
    LarsonScannerColor1(-0.01),
    LightChaseColor1(0.01),
    HeartbeatSlowColor1(0.03),
    HeartbeatMediumColor1(0.05),
    HeartbeatFastColor1(0.07),
    BreathSlowColor1(0.09),
    BreathFastColor1(0.11),
    ShotColor1(0.13),
    StrobeColor1(0.15),
    EndToEndBlendToBlackColor2(0.17),
    LarsonScannerColor2(0.19),
    LightChaseColor2(0.21),
    HeartbeatSlowColor2(0.23),
    HeartbeatMediumColor2(0.25),
    HeartbeatFastColor2(0.27),
    BreathSlowColor2(0.29),
    BreathFastColor2(0.31),
    ShotColor2(0.33),
    StrobeColor2(0.35),
    SparkleColor1OnColor2(0.37),
    SparkleColor2OnColor1(0.39),
    ColorGradientColor1And2(0.41),
    BeatsPerMinuteColor1And2(0.43),
    EndToEndBlendColor1To2(0.45),
    EndToEndBlend(0.47),
    Color1AndColor2NoBlending(0.49),
    TwinklesColor1And2(0.51),
    ColorWavesColor1And2(0.53),
    SinelonColor1And2(0.55),
    HotPink(0.57),
    DarkRed(0.59),
    Red(0.61),
    RedOrange(0.63),
    Orange(0.65),
    Gold(0.67),
    Yellow(0.69),
    LawnGreen(0.71),
    Lime(0.73),
    DarkGreen(0.75),
    Green(0.77),
    BlueGreen(0.79),
    Aqua(0.81),
    SkyBlue(0.83),
    DarkBlue(0.85),
    Blue(0.87),
    BlueViolet(0.89),
    Violet(0.91),
    White(0.93),
    Gray(0.95),
    DarkGray(0.97),
    Black(0.99);

    private final double value;

    private LedPattern(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
