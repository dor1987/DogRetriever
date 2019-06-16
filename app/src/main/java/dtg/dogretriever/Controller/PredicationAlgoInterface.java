package dtg.dogretriever.Controller;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import dtg.dogretriever.Model.PredictionRequestClass;
import dtg.dogretriever.Model.PredictionResponseClass;

public interface PredicationAlgoInterface {
    @LambdaFunction
    PredictionResponseClass LambdaPrediction(PredictionRequestClass request);
}
