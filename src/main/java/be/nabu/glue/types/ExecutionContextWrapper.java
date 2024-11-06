/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
