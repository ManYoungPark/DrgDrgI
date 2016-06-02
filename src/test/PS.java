package test;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//test
public class PS
{

	private static final int PRE_ACTUAL = 0;
	private static final int PRE_PREDICT = 1;
	private static final int PRE_PREDICT_PERCENT = 2;
	private static final int FVC = 0;
	private static final int FEV1 = 1;
	private static final int FEV1FVC = 2;

	private static final int FALSE = 0;
	private static final int TRUE = 1;
	private static final int NV = 5;
	private static final int CALCULATED = 9;

	 //public Double[][] PreValue = new Double[][] { { 5.46, 5.28, 104.0 }, {4.37, 4.35, 100.0 }, {80.0, 82.0, 97.0 } };
	 private Double[][] PreValue = new Double[][] { { 3.53, 3.01, 117.1 }, {3.23, 2.57, 125.3 }, {91.4,0.0, 0.0 } };

	 private  int[][] PreValueCheck = new int[][] { { 1, 1,1 }, { 1,1, 1 }, { 1, 0, 0 } };
//만영 ㅎㅎ
	 //만영 ㅎㅎ2
	 //만영 ㅎㅎ4
	
	public Double valueCheck_below100(Double tmp)
	{
		String tmp2 = Double.toString(tmp);
		tmp2 = tmp2.replace(".", "");
		String tmp4 = "";
		tmp2 = tmp2.substring(0, 1) + "." + tmp2.substring(1, tmp2.length());

		
		return Double.parseDouble(tmp2);
	}

	public static void main(String[] args)
	{/*

		PS a = new PS();
		System.out.println(a.valueCheck_below100(0.12));
		a.FVC_FEV1_FEF25_dataCheck();
		a.FVC_FEV1_FEF25_dataCheck();

		
		
		File file = new File("D:/PFT/00293701000000124520111101144246703.txt");

    	File fileToMove = new File("D:/PFT/real_al_pulmonary/00293701000000124520111101144246703.txt");
    	boolean isMoved = file.renameTo(fileToMove);
    	if(isMoved==true)
    		System.out.println("\n잘 이동함");
    	else System.out.println("\n이동하지 못함.");
*/
    	
    	String Al_patternPFT2th = "CHNG|CHG";// 이패턴은 필요없는 데이터.. 2번째꺼
		Matcher mcer = Pattern.compile(Al_patternPFT2th).matcher("ACTUAL	PRED	PPRED	ACTUAL	P PRED PCHNG"); // 그러면 두번째
		boolean a = mcer.find();
		if(a)
			System.out.println("aaa");
		if(a)
			System.out.println("bbb");
    	
    	
	}

	public void FVC_FEV1_FEF25_dataCheck()
	{

		int multi = 0;
		// 하나의 행에서 그냥 계산이 맞으면, 그행에 대한 3개의 셀에는 TRUE를 만듬.
		for (int k = 0; k < 3; k++)
		{
			if (PreValueCheck[k][0] == TRUE && PreValueCheck[k][1] == TRUE && PreValueCheck[k][2] == TRUE)
			{
				Double percent_cal = (PreValue[k][0] / PreValue[k][1]) * 100;

				if (percent_cal > 20)
					multi = 1;
				else
					multi = 10;

				// 계산된 값이 실제로 capture된것과 +-2 정도는 인정함.
				if (percent_cal * multi < (PreValue[k][2] * multi + 2) && percent_cal > (PreValue[k][2] * multi - 2))
				{
					System.out.println("correct.");

					for (int i = 0; i < 3; i++)
					{
						PreValueCheck[k][i] = TRUE;
					}

				} else
				// 어떤게 틀린지 모르니깐, 전부 NV로 해놓음..
				{
					for (int i = 0; i < 3; i++)
						PreValueCheck[k][i] = NV;
				}
			}

		}

		// 전부 true 이면, 바로 리턴 해서 끝내면 됨.

		// 여기서부터는 값이 하나라도 없었을때의 처리.

		Double tmp = 0.0, tmp3 = 0.0;

		multi = 0;

		// 행으로 먼저 체크함.
		for (int i = 0; i < 3; i++)
		{
			if (PreValueCheck[i][PRE_ACTUAL] != NV && PreValueCheck[i][PRE_PREDICT] != NV && PreValueCheck[i][PRE_PREDICT_PERCENT] != NV)
			{

				// actual이 값이 없을경우 , pred, pred%가 값이 있는데,
				if ((PreValueCheck[i][PRE_ACTUAL] == FALSE | PreValueCheck[i][PRE_ACTUAL] == CALCULATED | PreValueCheck[i][PRE_ACTUAL] == NV)
						&& PreValueCheck[i][PRE_PREDICT] != FALSE && PreValueCheck[i][PRE_PREDICT_PERCENT] != FALSE)
				{
					tmp3 = PreValue[i][PRE_PREDICT] * PreValue[i][PRE_PREDICT_PERCENT] * 0.01;
					PreValueCheck[i][PRE_ACTUAL] = CALCULATED;

					if (i == FVC)// FVC=FEV1/FEV1FVC*100
						tmp = PreValue[FEV1][PRE_ACTUAL] / PreValue[FEV1FVC][PRE_ACTUAL] * 100;
					if (i == FEV1)// FEV1=FVC*FEV1FVC/100
						tmp = PreValue[FVC][PRE_ACTUAL] * PreValue[FEV1FVC][PRE_ACTUAL] / 100;
					if (i == FEV1FVC)// FEV1FVC=FEV1/FVC*100
						tmp = PreValue[FEV1][PRE_ACTUAL] / PreValue[FVC][PRE_ACTUAL] * 100;

					if (tmp > 20)
						multi = 1;
					else
						multi = 10;

					if (tmp3 * multi < (tmp * multi + 2) && tmp3 * multi > (tmp * multi - 2))
					{// validation이
						// 맞으면, calculated에서 TRUE로 변경.
						PreValue[i][PRE_ACTUAL] = tmp3;

						for (int k = 0; k < 3; k++)
						{
							PreValueCheck[i][k] = TRUE;
							// PreValueCheck[k][0] = TRUE;
							PreValueCheck[k][PRE_ACTUAL] = TRUE;
						}
					} else
					{ // 계산이 맞지 않는다면, 두개 계산한것중에서 reference range 안에들어가는거 입력
						if (i == 0 | i == 1)
						{
							if (tmp3 > 1.0 && tmp3 < 12.0)
							{
								PreValue[i][PRE_ACTUAL] = tmp3;
								PreValueCheck[i][PRE_ACTUAL] = NV;
							} else if (tmp > 1.0 && tmp < 12.0)
							{
								PreValue[i][PRE_ACTUAL] = tmp;
								PreValueCheck[i][PRE_ACTUAL] = NV;
							} else
							{

								PreValue[i][PRE_ACTUAL] = 0.0;
								PreValueCheck[i][PRE_ACTUAL] = FALSE;
							}
						}
						if (i == 2)
						{
							if (tmp3 > 50 && tmp3 < 150.0)
							{
								PreValue[i][PRE_ACTUAL] = tmp3;
								PreValueCheck[i][PRE_ACTUAL] = NV;
							} else if (tmp > 50.0 && tmp < 150.0)
							{
								PreValue[i][PRE_ACTUAL] = tmp;
								PreValueCheck[i][PRE_ACTUAL] = NV;
							} else
							{
								PreValue[i][PRE_ACTUAL] = 0.0;
								PreValueCheck[i][PRE_ACTUAL] = FALSE;
							}

						}
					}

				}

				// predict 이 없는경우
				if (PreValueCheck[i][PRE_ACTUAL] != FALSE
						&& (PreValueCheck[i][PRE_PREDICT] == FALSE | PreValueCheck[i][PRE_PREDICT] == CALCULATED | PreValueCheck[i][PRE_PREDICT] == NV)
						&& PreValueCheck[i][PRE_PREDICT_PERCENT] != FALSE)
				{

					tmp3 =PreValue[i][PRE_ACTUAL] / PreValue[i][PRE_PREDICT_PERCENT] * 100;
					PreValueCheck[i][PRE_PREDICT] = CALCULATED;

					if (i == FVC)// FVC=FEV1/FEV1FVC*100
						tmp = PreValue[FEV1][PRE_PREDICT] / PreValue[FEV1FVC][PRE_PREDICT] * 100;
					if (i == FEV1)// FEV1=FVC*FEV1FVC/100
						tmp = PreValue[FVC][PRE_PREDICT] * PreValue[FEV1FVC][PRE_PREDICT] / 100;
					if (i == FEV1FVC)// FEV1FVC=FEV1/FVC*100
						tmp = PreValue[FEV1][PRE_PREDICT] / PreValue[FVC][PRE_PREDICT] * 100;

					if (tmp > 20)
						multi = 1;
					else
						multi = 10;

					if (tmp3 * multi < (tmp * multi + 2) && tmp3 * multi > (tmp * multi - 2))
					{// validation이
						// 맞으면, calculated에서 TRUE로 변경.
						for (int k = 0; k < 3; k++)
						{
							PreValueCheck[i][k] = TRUE;
							// PreValueCheck[k][1] = TRUE;
							PreValueCheck[k][PRE_PREDICT] = TRUE;

						}
					} else
					{ // 계산이 맞지 않는다면, 두개 계산한것중에서 reference range 안에들어가는거 입력
						if (i == 0 | i == 1)
						{
							if (tmp3 > 1.0 && tmp3 < 12.0)
							{
								PreValue[i][PRE_PREDICT] = tmp3;
								PreValueCheck[i][PRE_PREDICT] = NV;
							} else if (tmp > 1.0 && tmp < 12.0)
							{
								PreValue[i][PRE_PREDICT] = tmp;
								PreValueCheck[i][PRE_PREDICT] = NV;
							} else
							{
								PreValue[i][PRE_PREDICT] = 0.0;
								PreValueCheck[i][PRE_PREDICT] = FALSE;

							}

						}
						if (i == 2)
						{
							if (tmp3 > 50 && tmp3 < 150.0)
							{
								PreValue[i][PRE_PREDICT] = tmp3;
								PreValueCheck[i][PRE_PREDICT] = NV;
							} else if (tmp > 50.0 && tmp < 150.0)
							{
								PreValue[i][PRE_PREDICT] = tmp;
								PreValueCheck[i][PRE_PREDICT] = NV;
							} else
							{
								PreValue[i][PRE_PREDICT] = 0.0;
								PreValueCheck[i][PRE_PREDICT] = FALSE;

							}

						}

					}
				}
				//
				// predict percent가 없는경우..
				if (PreValueCheck[i][PRE_ACTUAL] != FALSE
						&& PreValueCheck[i][PRE_PREDICT] != FALSE
						&& (PreValueCheck[i][PRE_PREDICT_PERCENT] == FALSE | PreValueCheck[i][PRE_PREDICT_PERCENT] == CALCULATED | PreValueCheck[i][PRE_PREDICT_PERCENT] == NV))
				{

					tmp3 = PreValue[i][PRE_ACTUAL] / PreValue[i][PRE_PREDICT] * 100;
					PreValueCheck[i][PRE_PREDICT_PERCENT] = CALCULATED;

					if (i == FVC)// FVC=FEV1/FEV1FVC*100
						tmp = PreValue[FEV1][PRE_PREDICT_PERCENT] / PreValue[FEV1FVC][PRE_PREDICT_PERCENT] * 100;
					if (i == FEV1)// FEV1=FVC*FEV1FVC/100
						tmp = PreValue[FVC][PRE_PREDICT_PERCENT] * PreValue[FEV1FVC][PRE_PREDICT_PERCENT] / 100;
					if (i == FEV1FVC)// FEV1FVC=FEV1/FVC*100
						tmp = PreValue[FEV1][PRE_PREDICT_PERCENT] / PreValue[FVC][PRE_PREDICT_PERCENT] * 100;

					if (tmp > 20)
						multi = 1;
					else
						multi = 10;

					if (tmp3 * multi < (tmp * multi + 2) && tmp3 * multi > (tmp * multi - 2))
					{// validation이
						// 맞으면, calculated에서 TRUE로 변경.
						for (int k = 0; k < 3; k++)
						{
							PreValueCheck[i][k] = TRUE;
							// PreValueCheck[k][i] = TRUE;
							PreValueCheck[k][PRE_PREDICT_PERCENT] = TRUE;
						}
					} else
					{ // 계산이 맞지 않는다면, 두개 계산한것중에서 reference range 안에들어가는거 입력

						if (tmp3 > 50 && tmp3 < 150.0)
						{
							PreValue[i][PRE_PREDICT_PERCENT] = tmp3;
							PreValueCheck[i][PRE_PREDICT_PERCENT] = NV;
						} else if (tmp > 50.0 && tmp < 150.0)
						{
							PreValue[i][PRE_PREDICT_PERCENT] = tmp;
							PreValueCheck[i][PRE_PREDICT_PERCENT] = NV;
						} else
						{
							PreValue[i][PRE_PREDICT_PERCENT] = 0.0;
							PreValueCheck[i][PRE_PREDICT_PERCENT] = FALSE;

						}

					}
				}

			}// end of if

		}// end of for

		// ////////////////////////////////////////////////////inverse 된것도 검사
		// 하기//////////////////////////v//////////////////////////
		// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * for (int k = 0; k < 3; k++) { if (PreValueCheck[0][k] == TRUE &&
		 * PreValueCheck[1][k] == TRUE && PreValueCheck[2][k] == TRUE) { Double
		 * percent_cal = (PreValue[1][k] / PreValue[0][k]) * 100;
		 * 
		 * 
		 * multi = 1;
		 * 
		 * 
		 * // 계산된 값이 실제로 capture된것과 +-2 정도는 인정함. if (percent_cal * multi <
		 * (PreValue[2][k] * multi + 2) && percent_cal > (PreValue[2][k] * multi
		 * - 2)) { System.out.println("correct.");
		 * 
		 * 
		 * for (int i = 0; i < 3; i++) { PreValueCheck[i][k] = TRUE; }
		 * 
		 * 
		 * } else //어떤게 틀린지 모르니깐, 전부 NV로 해놓음.. { for (int i = 0; i < 3; i++)
		 * PreValueCheck[i][k] = NV; } }
		 * 
		 * 
		 * }
		 */

		double tmp2 = 0;
		for (int i = 0; i < 3; i++)
		{
			// actual이 값이 없을경우 , pred, pred%가 값이 있는데,
			// i==0일때는 actual 값이 없는 경우 i==1이면 pred 값이 없는 경우 , i==2 이면, pred% 가
			// 없는경우
			if ((PreValueCheck[FVC][i] == FALSE | PreValueCheck[FVC][i] == CALCULATED | PreValueCheck[FVC][i] == NV)
					&& PreValueCheck[FEV1][i] != FALSE && PreValueCheck[FEV1FVC][i] != FALSE)
			{
				tmp2 = PreValue[FEV1][i] / PreValue[FEV1FVC][i] * 100;
				PreValueCheck[FVC][i] = CALCULATED;

				if (i == PRE_ACTUAL)
					tmp = PreValue[FVC][PRE_PREDICT_PERCENT] * PreValue[FVC][PRE_PREDICT] / 100;
				if (i == PRE_PREDICT)
					tmp = PreValue[FVC][PRE_ACTUAL] / PreValue[FVC][PRE_PREDICT_PERCENT] * 100;
				if (i == PRE_PREDICT_PERCENT)
					tmp = PreValue[FVC][PRE_ACTUAL] / PreValue[FVC][PRE_PREDICT] * 100;

				if (tmp > 20)
					multi = 1;
				else
					multi = 10;

				if ((tmp2 * multi < tmp * multi + 2) && (tmp2 * multi > tmp * multi - 2))
				{

					for (int k = 0; k < 3; k++)
					{
						PreValue[FVC][i] = tmp2;
						PreValueCheck[i][k] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}

				} else if (tmp2 * multi < (tmp * multi + 2) && tmp2 * multi > (tmp * multi - 2))
				{// validation이
					// 맞으면, calculated에서 TRUE로 변경.
					for (int k = 0; k < 3; k++)
					{
						// PreValueCheck[i][k] = TRUE;
						PreValueCheck[FVC][i] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}
				} else
				{

					// 계산이 맞지 않는다면, 두개 계산한것중에서 reference range 안에들어가는거 입력
					if (i == 0 | i == 1)
					{
						if (tmp2 > 1.0 && tmp2 < 12.0)
						{
							PreValue[FVC][i] = tmp2;
							PreValueCheck[FVC][i] = NV;
						} else if (tmp > 1.0 && tmp < 12.0)
						{
							PreValue[FVC][i] = tmp;
							PreValueCheck[FVC][i] = NV;
						} else
						{

							PreValue[FVC][i] = 0.0;
							PreValueCheck[FVC][i] = FALSE;
						}
					}

					if (i == 2)
					{
						if (tmp2 > 50 && tmp2 < 150.0)
						{
							PreValue[FVC][i] = tmp2;
							PreValueCheck[FVC][i] = NV;

						} else if (tmp > 50.0 && tmp < 150.0)
						{
							PreValue[FVC][i] = tmp;
							PreValueCheck[FVC][i] = NV;
						} else
						{
							PreValue[FVC][i] = 0.0;
							PreValueCheck[FVC][i] = FALSE;
						}

					}

				}
			}
			if (PreValueCheck[FVC][i] != FALSE
					&& (PreValueCheck[FEV1][i] == FALSE | PreValueCheck[FEV1][i] == CALCULATED | PreValueCheck[FEV1][i] == NV)
					&& PreValueCheck[FEV1FVC][i] != FALSE)
			{
				// FVC=FEV1/FEV1FVC*100

				tmp2 = PreValue[FVC][i] * PreValue[FEV1FVC][i] / 100;
				PreValueCheck[FEV1][i] = CALCULATED;

				if (i == PRE_ACTUAL)
					tmp = PreValue[FEV1][PRE_PREDICT_PERCENT] * PreValue[FEV1][PRE_PREDICT] / 100;
				if (i == PRE_PREDICT)
					tmp = PreValue[FEV1][PRE_ACTUAL] / PreValue[FEV1][PRE_PREDICT_PERCENT] * 100;
				if (i == PRE_PREDICT_PERCENT)
					tmp = PreValue[FEV1][PRE_ACTUAL] / PreValue[FEV1][PRE_PREDICT] * 100;

				if (tmp > 20)
					multi = 1;
				else
					multi = 10;

				if ((tmp2 * multi < tmp * multi + 2) && (tmp2 * multi > tmp * multi - 2))
				{

					for (int k = 0; k < 3; k++)
					{
						PreValue[FEV1][i] = tmp2;
						PreValueCheck[i][k] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}

				} else if (tmp2 * multi < (tmp * multi + 2) && tmp2 * multi > (tmp * multi - 2)) // 계산하지
																																// 않고,
																																// 그냥
																																// 들어가
																																// 있는
																																// 값과
																																// 일치하면,
				{// validation이
					// 맞으면, calculated에서 TRUE로 변경.
					for (int k = 0; k < 3; k++)
					{
						// PreValueCheck[i][k] = TRUE;
						PreValueCheck[FEV1][i] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}
				} else
				{
					// 계산이 맞지 않는다면, 두개 계산한것중에서 reference range 안에들어가는거 입력
					if (i == 0 | i == 1)
					{
						if (tmp2 > 1.0 && tmp2 < 12.0)
						{
							PreValue[FEV1][i] = tmp2;
							PreValueCheck[FEV1][i] = NV;
						} else if (tmp > 1.0 && tmp < 12.0)
						{
							PreValue[FEV1][i] = tmp;
							PreValueCheck[FEV1][i] = NV;
						} else
						{

							PreValue[FEV1][i] = 0.0;
							PreValueCheck[FEV1][i] = FALSE;
						}
					}

					if (i == 2)
					{
						if (tmp2 > 50 && tmp2 < 150.0)
						{
							PreValue[FEV1][i] = tmp2;
							PreValueCheck[FEV1][i] = NV;

						} else if (tmp > 50.0 && tmp < 150.0)
						{
							PreValue[FEV1][i] = tmp;
							PreValueCheck[FEV1][i] = NV;
						} else
						{
							PreValue[FEV1][i] = 0.0;
							PreValueCheck[FEV1][i] = FALSE;
						}

					}

					// PreValue[FEV1][i] = tmp2;
				}
			}

			if (PreValueCheck[FVC][i] != FALSE && PreValueCheck[FEV1][i] != FALSE
					&& (PreValueCheck[FEV1FVC][i] == FALSE | PreValueCheck[FEV1FVC][i] == CALCULATED | PreValueCheck[FEV1FVC][i] == NV))
			{

				tmp2 = PreValue[FEV1][i] / PreValue[FVC][i] * 100;
				PreValueCheck[FEV1FVC][i] = CALCULATED;

				if (i == PRE_ACTUAL)
					tmp = PreValue[FEV1FVC][PRE_PREDICT_PERCENT] * PreValue[FEV1FVC][PRE_PREDICT] / 100;
				if (i == PRE_PREDICT)
					tmp = PreValue[FEV1FVC][PRE_ACTUAL] / PreValue[FEV1FVC][PRE_PREDICT_PERCENT] * 100;
				if (i == PRE_PREDICT_PERCENT)
					tmp = PreValue[FEV1FVC][PRE_ACTUAL] / PreValue[FEV1FVC][PRE_PREDICT] * 100;

				if (tmp > 20)
					multi = 1;
				else
					multi = 10;

				if ((tmp2 * multi < tmp * multi + 2) && (tmp2 * multi > tmp * multi - 2))
				{

					for (int k = 0; k < 3; k++)
					{
						PreValue[FEV1FVC][i] = tmp2;
						PreValueCheck[i][k] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}

				} else if (tmp2* multi < (tmp * multi + 2) && tmp2* multi > (tmp * multi - 2))
				// 계산하지// 않고,// 그냥// 들어가// 있는// 값과// 일치하면,
				{// validation이
					// 맞으면, calculated에서 TRUE로 변경.
					for (int k = 0; k < 3; k++)
					{
						// PreValueCheck[i][k] = TRUE;
						PreValueCheck[FEV1FVC][i] = TRUE;
						PreValueCheck[k][i] = TRUE;
					}
				} else
				{
					if (tmp2 > 50 && tmp2 < 150.0)
					{
						PreValue[FEV1FVC][i] = tmp2;
						PreValueCheck[FEV1FVC][i] = NV;

					} else if (tmp > 50.0 && tmp < 150.0)
					{
						PreValue[FEV1FVC][i] = tmp;
						PreValueCheck[FEV1FVC][i] = NV;
					} else
					{
						PreValue[FEV1FVC][i] = 0.0;
						PreValueCheck[FEV1FVC][i] = FALSE;
					}

					// PreValue[FEV1FVC][i] = tmp2;
				}
			}
		}
		
		
		
		// 마지막 값체크.. reference range 확인.. 벗어난거 있으면, 전부 false로
		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 3; k++)
			{

				if (k == 2 | i == 2)
				{
					if (PreValue[i][k] < 40 | PreValue[i][k] > 150.0)
					{
						PreValueCheck[i][k] = FALSE;
					}
				}else if (PreValue[i][k] < 1.0 | PreValue[i][k] > 15.0)
				{
					PreValueCheck[i][k] = FALSE;
				}

			
					

			}
			
		}
		
		

		System.out.print("FVC :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValue[0][i] + " ");
		}
		System.out.println("");

		System.out.print("FEV1 :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValue[1][i] + " ");
		}
		System.out.println("");
		System.out.print("FEV1/FVC :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValue[2][i] + " ");
		}
		System.out.println("");

		System.out.print("FVC :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValueCheck[0][i] + " ");
		}
		System.out.println("");

		System.out.print("FEV1 :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValueCheck[1][i] + " ");
		}
		System.out.println("");
		System.out.print("FEV1/FVC :");
		for (int i = 0; i < 3; i++)
		{
			System.out.print(PreValueCheck[2][i] + " ");
		}
		System.out.println("");

	}

}
