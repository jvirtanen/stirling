/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fixengine.tags.bats.europe;

import fixengine.messages.bats.europe.IDSourceValue;
import fixengine.messages.EnumField;
import fixengine.messages.Required;
import fixengine.messages.Tag;

public class IDSource extends Tag<IDSource.Field> {
    public static final IDSource TAG = new IDSource();

    public IDSource() {
        super(59, Field.class);
    }

    public static class Field extends EnumField<IDSourceValue> {
        public Field(Tag<Field> tag) {
            super(tag, Required.YES);
        }

        @Override public void parse(String value) {
            this.value = IDSourceValue.parse(value.charAt(0));
        }
    }
}
