package dtg.dogretriever.Controller;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import dtg.dogretriever.Model.RequestClass;
import dtg.dogretriever.Model.ResponseClass;

public interface MyInterface {
    @LambdaFunction
    ResponseClass AndroidBackendLambdaFunction(RequestClass request);
}
