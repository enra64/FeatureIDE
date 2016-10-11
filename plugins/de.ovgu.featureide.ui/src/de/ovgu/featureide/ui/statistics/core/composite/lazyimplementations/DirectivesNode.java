/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations;

import static de.ovgu.featureide.fm.core.localization.StringTable.AVERAGE_FEATURES_PER_DIRECTIVE;
import static de.ovgu.featureide.fm.core.localization.StringTable.AVERAGE_NUMBER_OF_DIRECTIVES_PER_CLASS;
import static de.ovgu.featureide.fm.core.localization.StringTable.CLASS_STATISTICS;
import static de.ovgu.featureide.fm.core.localization.StringTable.DIRECTIVES_PER_CLASS;
import static de.ovgu.featureide.fm.core.localization.StringTable.FEATURES_PER_DIRECTIVE;
import static de.ovgu.featureide.fm.core.localization.StringTable.IN_CLASS;
import static de.ovgu.featureide.fm.core.localization.StringTable.MAXIMUM_FEATURES_PER_DIRECTIVE;
import static de.ovgu.featureide.fm.core.localization.StringTable.MAXIMUM_NESTING_OF_DIRECTIVES;
import static de.ovgu.featureide.fm.core.localization.StringTable.MAXIMUM_NUMBER_OF_DIRECTIVES;
import static de.ovgu.featureide.fm.core.localization.StringTable.MINIMUM_FEATURES_PER_DIRECTIVE;
import static de.ovgu.featureide.fm.core.localization.StringTable.MINIMUM_NUMBER_OF_DIRECTIVES;
import static de.ovgu.featureide.fm.core.localization.StringTable.NUMBER_OF_DIRECTIVES;
import static de.ovgu.featureide.fm.core.localization.StringTable.PROJECT_STATISTICS;

import java.util.Map;

import de.ovgu.featureide.core.fstmodel.FSTClass;
import de.ovgu.featureide.core.fstmodel.FSTModel;
import de.ovgu.featureide.ui.statistics.core.composite.LazyParent;
import de.ovgu.featureide.ui.statistics.core.composite.Parent;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.genericdatatypes.AbstractSortModeNode;

/**
 * TreeNode who stores the number of used preprocessor directives, directives
 * per class and features per directives.<br>
 * This node should only be used for a preprocessor project.
 * 
 * @author Dominik Hamann
 * @author Patrick Haese
 */
public class DirectivesNode extends LazyParent {
	
	protected final FSTModel fstModel;

	/**
	 * Constructor for a {@code DirectivesNode}.
	 * 
	 * @param description
	 *            description of the node shown in the view
	 * @param fstModel
	 *            FSTModel for the calculation
	 */
	public DirectivesNode(String description, FSTModel fstModel) {
		super(description);
		this.fstModel = fstModel;
	}

	@Override
	protected void initChildren() {
		Parent project = new Parent(PROJECT_STATISTICS);
		final Aggregator aggProject = new Aggregator();
		aggProject.processAll(fstModel);
		
		// 1.1 Project statistics
		// 1.1.1 Number of Directives Node
		Parent directives = new Parent(NUMBER_OF_DIRECTIVES);
		directives.setValue(aggProject.getDirectiveCount());
		project.addChild(directives);
		
		// 1.1.2 Directives per class
		final Map.Entry<String,Integer> maximumSum = aggProject.getMaximumNumberOfDirectives();
		final Map.Entry<String,Integer> minimumSum = aggProject.getMinimumNumberOfDirectives();
		final Double averageSum = aggProject.getAverageNumberOfDirectives();

		final Parent directivesPerClass = new Parent(DIRECTIVES_PER_CLASS);
		// 1.1.2.1 Maximum number of directives:
		directivesPerClass.addChild(new DirectivesLeafNode(MAXIMUM_NUMBER_OF_DIRECTIVES + maximumSum.getValue() + IN_CLASS + maximumSum.getKey(), fstModel.getFeatureProject(), maximumSum.getKey()));
		// 1.1.2.2 Minimum number of directives:
		directivesPerClass.addChild(new DirectivesLeafNode(MINIMUM_NUMBER_OF_DIRECTIVES + minimumSum.getValue() + IN_CLASS + minimumSum.getKey(), fstModel.getFeatureProject(), minimumSum.getKey()));
		// 1.1.2.3 Average number of directives per class:
		directivesPerClass.addChild(new Parent(AVERAGE_NUMBER_OF_DIRECTIVES_PER_CLASS, averageSum));
		project.addChild(directivesPerClass);

		// 1.1.3 Features per directive
		final Parent featuresPerDirectives = new Parent(FEATURES_PER_DIRECTIVE);
		project.addChild(featuresPerDirectives);
		
		final Map.Entry<String,Integer> maximumNumberOfFeatures = aggProject.getMaxNumberOfFeatures();
		final Map.Entry<String,Integer> minimumNumberOfFeatures = aggProject.getMinNumberOfFeatures();
		final Double averageNumberOfFeatures = aggProject.getAverageNumberOfFeatures();
		
		// 1.1.3.1 Maximum number of features per directive:
		featuresPerDirectives.addChild(new DirectivesLeafNode(MAXIMUM_FEATURES_PER_DIRECTIVE, maximumNumberOfFeatures.getValue() + IN_CLASS + maximumNumberOfFeatures.getKey(), fstModel.getFeatureProject(), maximumNumberOfFeatures.getKey()));
		// 1.1.3.2 Maximum number of features per directive:
		featuresPerDirectives.addChild(new DirectivesLeafNode(MINIMUM_FEATURES_PER_DIRECTIVE, minimumNumberOfFeatures.getValue() + IN_CLASS + minimumNumberOfFeatures.getKey(), fstModel.getFeatureProject(), minimumNumberOfFeatures.getKey()));
		// 1.1.3.3 Average number of features per directives:
		featuresPerDirectives.addChild(new Parent(AVERAGE_FEATURES_PER_DIRECTIVE, averageNumberOfFeatures));

		final Map.Entry<String,Integer> maxNesting = aggProject.getMaxNesting();
		int line = aggProject.getLineFromNesting(maxNesting.getValue());
		// 1.1.4 Maximum nesting of directives: 
		project.addChild(new DirectivesLeafNode(MAXIMUM_NESTING_OF_DIRECTIVES, maxNesting.getValue() + IN_CLASS + maxNesting.getKey(), fstModel.getFeatureProject(), maxNesting.getKey(), line));

		addChild(project);
		
		// 1.2 Class Statistics Node
		Parent classes = new AbstractSortModeNode(CLASS_STATISTICS) {
			@Override
			protected void initChildren() {
				for(FSTClass c : fstModel.getClasses()){
					String className = c.getName();
					final int pIndex = className.lastIndexOf('/');
					className = ((pIndex > 0) ? className.substring(0, pIndex + 1).replace('/', '.') : "(default package).") + className.substring(pIndex + 1);
					Parent p = new Parent(className, aggProject.getDirectiveCountForClass(c.getName()));
					//-------------------
					int line = aggProject.getLineFromNesting(aggProject.getNestingCountForClass(c.getName()));
					//-------------------
					p.addChild(new DirectivesLeafNode(MAXIMUM_NESTING_OF_DIRECTIVES, aggProject.getNestingCountForClass(c.getName()), fstModel.getFeatureProject(), c.getName(), line));
					addChild(p);
				}
				
			}
		};
		addChild(classes);
		addChild(new StatisticsProgramSizeNew(NUMBER_OF_CODELINES, fstModel, fstModel.getFeatureProject()));
	}
}
