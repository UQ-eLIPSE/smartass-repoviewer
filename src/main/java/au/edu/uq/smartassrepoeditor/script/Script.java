/* This file is part of SmartAss and describes the Script class that is the part of SmartAss script processor.
 * Copyright (C) 2006 Department of Mathematics, The University of Queensland
 * SmartAss is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2, or
 * (at your option) any later version.
 * GNU program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with program;
 * see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package au.edu.uq.smartassrepoeditor.script;

import au.edu.uq.smartassrepoeditor.engine.*;

/**
 * The Script class that is the abstract ancestor for any SmartAss script processor class.
 */
public abstract class Script {
 	Engine engine;
 	
 	Script(Engine engine) 
 	{
     	this.engine = engine;
 	}
 	
 	/**
 	 * Override this method to execute the block of script code in some specific script language.  
 	 * 
 	 * @param block		the block of code
 	 * 
 	 * @return			execution result as the {@link String}
 	 */
 	abstract public String executeBlock(String block);
 }
