package dtg.dogretriever.Presenter;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface MyInterface {
    @LambdaFunction
    ResponseClass  AndroidBackendLambdaFunction(RequestClass request);
}
