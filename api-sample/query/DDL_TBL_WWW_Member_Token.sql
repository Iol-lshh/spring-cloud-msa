------------------------
create table TBL_WWW_Member_Token_TEMP(
	[refresh] [varchar](300) NOT NULL,
	[access] [varchar](300) NOT NULL,
	[UserID] [varchar](50) NOT NULL,
	[DelYn] [char](1) NOT NULL,
	[RegDate] [datetime] NOT NULL,

 CONSTRAINT [PK_TBL_WWW_Member_Token_TEMP] PRIMARY KEY CLUSTERED 
(
	[refresh] ASC
)WITH (PAD_INDEX = ON, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 70, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY];


ALTER TABLE [dbo].[TBL_WWW_Member_Token_TEMP] ADD  CONSTRAINT [DF_TBL_WWW_Member_Token_TEMP_Del_Yn]  DEFAULT ('N') FOR [DelYn];
ALTER TABLE [dbo].[TBL_WWW_Member_Token_TEMP] ADD  CONSTRAINT [DF_TBL_WWW_Member_Token_TEMP_RegDate]  DEFAULT (getdate()) FOR [RegDate];





-----------------------
create table TBL_WWW_Member_Disabled_Access_Token_TEMP(
	[access] [varchar](300) NOT NULL,
	[RegDate] [datetime] NOT NULL, 
	CONSTRAINT [PK_TBL_WWW_Member_Disabled_Access_Token_TEMP] PRIMARY KEY CLUSTERED 
(
	[access] ASC
)WITH (PAD_INDEX = ON, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, FILLFACTOR = 70, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY];

ALTER TABLE [dbo].[TBL_WWW_Member_Disabled_Access_Token_TEMP] ADD  CONSTRAINT [DF_TBL_WWW_Member_Disabled_Access_Token_TEMP_RegDate]  DEFAULT (getdate()) FOR [RegDate];

