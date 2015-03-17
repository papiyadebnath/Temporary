package com.deere.content;

import static org.apache.velocity.app.Velocity.getTemplate;
import static org.apache.velocity.app.Velocity.init;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class Velocity {
	public Velocity() throws Exception {
		init();
	}

	public String parseContent(Map<String, String> data, String templateName) throws Exception {
		VelocityContext context = new VelocityContext();

		Template template = getTemplate(templateName);
		StringWriter sw = new StringWriter();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		template.merge(context, sw);
		return sw.toString();
	}
}
