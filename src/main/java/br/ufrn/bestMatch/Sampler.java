package br.ufrn.bestMatch;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.Serializable;

public class Sampler extends AbstractJavaSamplerClient implements Serializable {
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String var1 = javaSamplerContext.getParameter("path");
        String var2 = javaSamplerContext.getParameter("text");
        SampleResult result = new SampleResult();
        result.sampleStart();
        result.setSampleLabel("Test Sample");
        ThreadManager threadManager = new ThreadManager(var1, var2);
        if (threadManager.start().getDistance() == 0) {
            result.sampleEnd();
            result.setResponseCode("200");
            result.setResponseMessage("OK");
            result.setSuccessful(true);
        } else {
            result.sampleEnd();
            result.setResponseCode("500");
            result.setResponseMessage("NOK");
            result.setSuccessful(false);
        }
        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("path", "./small_file.txt");
        defaultParameters.addArgument("text", "test");
        return defaultParameters;
    }
}
