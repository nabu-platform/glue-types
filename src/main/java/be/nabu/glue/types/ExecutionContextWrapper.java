package be.nabu.glue.types;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.libs.types.ComplexContentWrapperFactory;
import be.nabu.libs.types.api.ComplexContent;
import be.nabu.libs.types.api.ComplexContentWrapper;

public class ExecutionContextWrapper implements ComplexContentWrapper<ExecutionContext> {

	@Override
	public Class<ExecutionContext> getInstanceClass() {
		return ExecutionContext.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComplexContent wrap(ExecutionContext context) {
		return ComplexContentWrapperFactory.getInstance().getWrapper().wrap(context.getPipeline());
	}

}
