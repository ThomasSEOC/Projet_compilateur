package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;

public class DoubleDefException extends LocationException {
        private static final long serialVersionUID = -8122514996569278575L;

        public DoubleDefException(String message, Location location) {
            super(message, location);
        }

}
