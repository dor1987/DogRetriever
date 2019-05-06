package dtg.dogretriever.Presenter;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface PredicationAlgoInterface {
    @LambdaFunction
    PredictionResponseClass  LambdaPrediction(PredictionRequestClass request);

}
