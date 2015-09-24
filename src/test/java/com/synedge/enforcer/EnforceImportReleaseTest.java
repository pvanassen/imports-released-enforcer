package com.synedge.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Paul van Assen
 */
public class EnforceImportReleaseTest {

    @Test(expected = EnforcerRuleException.class)
    public void testExecuteIncorrectPom() throws Exception {
        EnforceImportRelease rule = new EnforceImportRelease();
        Model model = null;
        Reader reader;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream("/incorrect-pom.xml"));
            model = mavenreader.read(reader);
        } catch (Exception ignored) {
        }
        MavenProject project = new MavenProject(model);
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        when(helper.evaluate(eq("${project}"))).thenReturn(project);
        rule.execute(helper);
    }

    @Test
    public void testExecuteCorrectPom() throws Exception {
        EnforceImportRelease rule = new EnforceImportRelease();
        Model model = null;
        Reader reader;
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        try {
            reader = new InputStreamReader(getClass().getResourceAsStream("/correct-pom.xml"));
            model = mavenreader.read(reader);
        } catch (Exception ignored) {
        }
        MavenProject project = new MavenProject(model);
        EnforcerRuleHelper helper = mock(EnforcerRuleHelper.class);
        when(helper.evaluate(eq("${project}"))).thenReturn(project);
        rule.execute(helper);
    }
}