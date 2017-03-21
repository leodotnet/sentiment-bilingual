build:
	ant clean
	ant build
init:
	python scripts/init.py
	chmod +x *.sh
check:
	./check.sh
movelog:
	mv *.log logs/.

aspect_S1:
	nohup ./exp_aspect_S1.sh en semeval  &
aspect_S1_ex:
	nohup ./exp_aspect_S1_ex.sh en semeval  &
aspect_baseline1:
	nohup ./exp_aspect_baseline1.sh en semeval &
aspect_baseline2:
	nohup ./exp_aspect_baseline2.sh en semeval &
aspect_baseline3:
	nohup ./exp_aspect_baseline3.sh en semeval &
aspect_singlescope:
	nohup ./exp_aspect_singlescope.sh en semeval &
aspect_fixscope:
	nohup ./exp_aspect_fixscope.sh en semeval &
aspect_baseline21:
	nohup ./exp_aspect_baseline21.sh en semeval &
aspect_baseline2_es:
	nohup ./exp_aspect_baseline2.sh es semeval &
aspect_baseline21_es:
	nohup ./exp_aspect_baseline21.sh es semeval &

ptb_static_dev:
	nohup ./exp_stb_statictree.sh en dev &
ptb_static_wordembedding_dev:
	nohup ./exp_stb_statictree_wordembedding.sh	en dev &
sentimentspan_nonlatent_crfnn_english:
	nohup ./exp_sentimentspan_nonlatent_crfnn.sh en dev &


baseline_joint_english:
	nohup ./exp_baseline_joint.sh en dev &
sentimentspan_latent_english_dev:
	nohup ./exp_sentimentspanlatent.sh en dev &
sentimentspan_latent_wordembedding_english_dev:
	nohup ./exp_sentimentspanlatent_wordembedding.sh en dev &

sentimentspan_latent_english:
	nohup ./exp_sentimentspanlatent.sh en full &
sentimentspan_latent_nohiddeninfo_english:
	nohup ./exp_sentimentspanlatent_nohiddeninfo.sh en full &
sentimentspan_latent_withpostag_english:
	nohup ./exp_sentimentspanlatent_withpostag.sh en full &
sentimentspan_latent_wordembedding_english:
	nohup ./exp_sentimentspanlatent_wordembedding.sh en full &
sentimentspan_nonlatent_english:
	nohup ./exp_sentimentspan_nonlatent.sh en full &
sentimentspan_semimarkov_nonlatent_english:
	nohup ./exp_semimarkov.sh en full &
sentimentspan_semimarkov_latent_wordembedding_english:
	nohup ./exp_semimarkovlatent_wordembedding.sh en full &
sentimentspan_semimarkov_latent_english:
	nohup ./exp_semimarkovlatent.sh en full &
baseline_collapse_english:
	nohup ./exp_baseline_collapse.sh en full &
baseline_pipeline_english:
	nohup ./exp_baseline_pipeline.sh en full &


sentimentspan_latent_extend_english:
	nohup ./exp_sentimentspanlatent_extend.sh en full &


sentimentspan_latent_spanish:
	nohup ./exp_sentimentspanlatent.sh es full &
sentimentspan_latent_nohiddeninfo_spanish:
	nohup ./exp_sentimentspanlatent_nohiddeninfo.sh es full &
sentimentspan_latent_withpostag_spanish:
	nohup ./exp_sentimentspanlatent_withpostag.sh es full &
sentimentspan_latent_wordembedding_spanish:
	nohup ./exp_sentimentspanlatent_wordembedding.sh es full &
sentimentspan_nonlatent_spanish:
	nohup ./exp_sentimentspan_nonlatent.sh es full &
sentimentspan_semimarkov_nonlatent_spanish:
	nohup ./exp_semimarkov.sh es full &
sentimentspan_semimarkov_latent_spanish:
	nohup ./exp_semimarkovlatent.sh es full &
sentimentspan_semimarkov_latent_wordembedding_spanish:
	nohup ./exp_semimarkovlatent_wordembedding.sh es full &
baseline_collapse_spanish:
	nohup ./exp_baseline_collapse.sh es full &
baseline_pipeline_spanish:
	nohup ./exp_baseline_pipeline.sh es full &


sentimentspan_latent_extend_spanish:
	nohup ./exp_sentimentspanlatent_extend.sh es full &


