/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.emitters;

import com.google.common.base.Optional;
import org.raml.api.RamlResourceMethod;
import org.raml.jaxrs.common.Example;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 4/16/17. Just potential zeroes and ones
 */
public class ExampleEmitter implements LocalEmitter {


  private final IndentedAppendable writer;
  private boolean headerDone = false;

  public ExampleEmitter(IndentedAppendable writer) {
    this.writer = writer;
  }

  @Override
  public void emit(RamlType ramlType) throws IOException {

    if (!hasAnExample(ramlType)) {

      return;
    }

    boolean currentHeaderDone = headerDone;
    if (!headerDone) {
      writer.appendLine("example:");
      writer.indent();
      writer.appendLine("strict: false");
      writer.appendLine("value:");
      writer.indent();
      headerDone = true;
    }

    for (RamlProperty ramlProperty : ramlType.getProperties()) {
      ramlProperty.emit(this);
    }

    if (!currentHeaderDone) {
      writer.outdent();
      writer.outdent();
      headerDone = true;
    }

  }

  @Override
  public void emit(RamlProperty ramlProperty) throws IOException {

    RamlType ramlType = ramlProperty.getRamlType();
    if (!ramlType.isRamlScalarType()) {

      writer.appendLine(ramlProperty.getName() + ":");
      writer.indent();

      ramlType.emit(this);

      writer.outdent();
    } else {

      Optional<Example> e = ramlProperty.getAnnotation(Example.class);
      if (!e.isPresent()) {

        return;
      }

      writer.appendLine(ramlProperty.getName() + ": " + e.get().value());
    }

  }

  private boolean hasAnExample(RamlType type) {

    for (RamlProperty ramlProperty : type.getProperties()) {
      if (ramlProperty.getAnnotation(Example.class).isPresent()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void emit(RamlResourceMethod method) throws IOException {

  }
}
